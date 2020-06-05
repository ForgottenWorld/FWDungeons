package it.forgottenworld.dungeons.controller

import it.forgottenworld.dungeons.cui.formatInvitation
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.getDungeonInstance
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player
import java.util.*

object FWDungeonsController {
    val dungeons = mutableMapOf<Int, Dungeon>()
    val activeDungeons = mutableMapOf<Int, Boolean>()
    val parties = mutableMapOf<Int, Party>()
    val playerParties = mutableMapOf<UUID, Party>()
    val playersTriggering = mutableMapOf<UUID, Trigger>()

    fun getMaxDungeonId() = dungeons.keys.max() ?: -1
    private fun getMaxPartyId(): Int = parties.keys.max() ?: -1
    fun getDungeonById(id: Int) = dungeons[id]

    fun playerJoinInstance(player: Player, instanceId: Int, dungeonId: Int, partyKey: String): Int {
        val dungeon = getDungeonById(dungeonId) ?: return -1 //invalid dungeon id
        val instance = dungeon.instances.find { it.id == instanceId } ?: return -2 //invalid instance id

        if (playerParties[player.uniqueId] != null)
            return -3 //player already in party

        val party = instance.party

        if (party == null) {
            instance.party = Party(
                    getMaxPartyId() + 1,
                    mutableListOf(player),
                    player,
                    dungeon.numberOfPlayers.last,
                    false,
                    instance
            ).also {
                parties[it.id] = it
                playerParties[player.uniqueId] = it
            }
            return 0 //party created, player is leader
        } else {
            if (party.isFull) return -4 //party is full
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
            it.playerLeave(player)
            0 //party left succesfully
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
                else -> -2
            } //player is not party leader
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
}