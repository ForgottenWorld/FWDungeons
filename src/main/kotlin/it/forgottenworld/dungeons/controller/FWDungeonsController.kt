package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.cui.formatInvitation
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.getDungeonInstance
import it.forgottenworld.dungeons.utils.getParty
import org.bukkit.Bukkit.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object FWDungeonsController {
    val dungeons = mutableMapOf<Int, Dungeon>()
    val activeDungeons = mutableMapOf<Int, Boolean>()
    val playerParties = mutableMapOf<UUID, Party>()
    val playersTriggering = mutableMapOf<UUID, Trigger>()
    val playerReturnPositions = mutableMapOf<UUID, Location>()
    val playerReturnGameModes = mutableMapOf<UUID, GameMode>()

    fun getMaxDungeonId() = dungeons.keys.max() ?: -1
    fun getDungeonById(id: Int) = dungeons[id]

    fun playerJoinInstance(player: Player, instanceId: Int, dungeonId: Int, partyKey: String): Int {
        val dungeon = getDungeonById(dungeonId) ?: return -1 //invalid dungeon id
        val instance = dungeon.instances.find { it.id == instanceId } ?: return -2 //invalid instance id

        if (playerParties[player.uniqueId] != null)
            return -3 //player already in party

        val party = instance.party

        if (party == null) {
            instance.party = Party(
                    mutableListOf(player),
                    player,
                    dungeon.numberOfPlayers.last,
                    false,
                    instance
            ).also {
                playerParties[player.uniqueId] = it
            }
            return 0 //party created, player is leader
        } else {
            if (party.isFull) return -4 //party is full
            if (party.inGame) return -7 //party is inside the dungeon
            if (instance.party!!.isLocked && partyKey != instance.party!!.partyKey) return -5 //wrong key
            return if (party.playerJoin(player)) 1  //party joines, player is not leader
            else -6 //join failed
        }
    }

    fun playerSendInvite(player: Player, to: String): Int {
        val instance = player.getDungeonInstance() ?: return -1 //player is not in an instance
        val party = instance.party
        if (party?.leader != player) return -2 //player is not party leader
        val toPlayer = getServer().getPlayer(to) ?: return -3 //receiver does not exist

        toPlayer.spigot().sendMessage(formatInvitation(
                player.name,
                instance.dungeon.id,
                instance.id,
                party.partyKey
        ))

        return 0
    }

    fun playerLeaveParty(player: Player): Int {
        return playerParties[player.uniqueId]?.let {
            if (it.inGame) -2 //already inside the dungeon
            else {
                it.playerLeave(player)
                0 //party left succesfully
            }
        } ?: -1 //player is not in a party
    }

    fun playerLockParty(player: Player): Int {
        return playerParties[player.uniqueId]?.let {
            when {
                it.isLocked -> -3 //party is already locked
                player == it.leader -> {
                    it.lock()
                    0 //party locked succesfully
                }
                else -> -2 //player is not party leader
            }
        } ?: -1 //player is not in a party
    }

    fun playerUnlockParty(player: Player): Int {
        return playerParties[player.uniqueId]?.let {
            when {
                !it.isLocked -> -3 //party is already unlocked
                (player == it.leader) -> {
                    it.unlock()
                    0 //party unlocked succesfully
                }
                else -> -2 //player is not party leader
            }
        } ?: -1 //player is not in a party
    }

    fun playerStart(player: Player): Int {
        return playerParties[player.uniqueId]?.let {
            when {
                it.leader != player -> -2 //player is not party leader
                it.players.count() < it.instance.dungeon.numberOfPlayers.first -> -3 //not enough players
                else -> {
                    it.inGame = true
                    it.players.forEach { p ->
                        playerReturnPositions[p.uniqueId] = p.location
                        playerReturnGameModes[p.uniqueId] = p.gameMode
                        p.gameMode = GameMode.ADVENTURE
                        p.teleport(
                                Location(
                                        getWorld(ConfigManager.dungeonWorld),
                                        it.instance.startingPostion.x,
                                        it.instance.startingPostion.y,
                                        it.instance.startingPostion.z))
                        p.sendMessage("Good luck out there!")
                    }
                    0 //all players teleported to instance starting position
                }
            }
        } ?: -1 //player is not in a party
    }

    fun evacuateDungeon(dungeonId: Int, instanceId: Int): Boolean {
        dungeons[dungeonId]?.instances?.find { it.id == instanceId }?.onInstanceFinish(false) ?: return false
        return true
    }

    fun lookupPlayer(playerName: String): String {
        val player = getPlayer(playerName) ?: return "Player not found"
        val party = player.getParty() ?: return "Player is not in a party or an instance"
        return "Player $playerName is in a party for dungeon id ${party.instance.dungeon.id}, instance id ${party.instance.id}"
    }
}