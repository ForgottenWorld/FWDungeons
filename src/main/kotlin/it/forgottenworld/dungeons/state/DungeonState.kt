package it.forgottenworld.dungeons.state

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.cui.StringConst
import it.forgottenworld.dungeons.cui.formatInvitation
import it.forgottenworld.dungeons.cui.getString
import it.forgottenworld.dungeons.db.executeQuery
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.dungeon.Dungeon
import it.forgottenworld.dungeons.model.dungeon.DungeonInstance
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.utils.getDungeonInstance
import it.forgottenworld.dungeons.utils.getParty
import org.bukkit.Bukkit.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.util.*

object DungeonState {
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

        if (activeDungeons[dungeonId] != true) {
            return -8 //dungeon is disabled
        }

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
            return if (party.playerJoin(player)) 1  //party joined, player is not leader
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
                        p.sendMessage("${getString(StringConst.CHAT_PREFIX)}Good luck out there!")
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
        val player = getPlayer(playerName) ?: return "${getString(StringConst.CHAT_PREFIX)}Player not found"
        val party = player.getParty() ?: return "${getString(StringConst.CHAT_PREFIX)}Player is not in a party or an instance"
        return "${getString(StringConst.CHAT_PREFIX)}Player $playerName is in a party for dungeon (id: ${party.instance.dungeon.id}), instance (id: ${party.instance.id})"
    }

    fun playerEnableDungeon(dungeonId: Int) : Boolean {
        return if (dungeons.contains(dungeonId)) {
            activeDungeons[dungeonId] = true
            true
        } else false
    }

    fun playerDisableDungeon(dungeonId: Int) : Boolean {
        return dungeons[dungeonId]?.let {
            it.instances.forEach { inst -> evacuateDungeon(it.id, inst.id) }
            activeDungeons[dungeonId] = false
            true
        } ?: false
    }

    fun playerReload() {
        dungeons.values.forEach { it.instances.forEach { inst -> evacuateDungeon(it.id, inst.id) } }
        dungeons.clear()
        activeDungeons.clear()
        playerParties.clear()
        playersTriggering.clear()
        playerReturnPositions.clear()
        playerReturnGameModes.clear()

        loadData()
    }
}

fun loadData() {
    ConfigManager.loadConfig(FWDungeonsPlugin.config)
    ConfigManager.loadDungeonConfigs(FWDungeonsPlugin.dataFolder)
    getInstancesFromDB()
}


private fun getInstancesFromDB() {
    executeQuery("SELECT * FROM fwd_instance_locations;") { res ->
        while (res.next()) {
            val dungeon = DungeonState.dungeons[res.getInt("dungeon_id")]
            val instOrigin = BlockVector(
                    res.getInt("x"),
                    res.getInt("y"),
                    res.getInt("z"))
            dungeon?.instances?.add(
                    DungeonInstance(
                            res.getInt("instance_id"),
                            dungeon,
                            instOrigin,
                            dungeon.triggers.map {
                                Trigger(it.id,
                                        dungeon,
                                        it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                                        it.effectParser,
                                        it.requiresWholeParty
                                ).apply { label = it.label}
                            }.toMutableList(),
                            dungeon.activeAreas.map {
                                ActiveArea(it.id,
                                        it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                                        it.startingMaterial
                                ).apply { label = it.label}
                            }.toMutableList()
                    ).apply{
                        triggers.forEach { it.parseEffect(this) }
                        resetInstance()
                    }
            )
        }
    }
}