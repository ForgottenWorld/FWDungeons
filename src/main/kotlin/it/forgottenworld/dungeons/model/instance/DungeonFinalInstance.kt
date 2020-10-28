package it.forgottenworld.dungeons.model.instance

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getLockClickable
import it.forgottenworld.dungeons.cli.getString
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.manager.DungeonManager.returnGameMode
import it.forgottenworld.dungeons.manager.DungeonManager.returnPosition
import it.forgottenworld.dungeons.manager.InstanceObjectiveManager
import it.forgottenworld.dungeons.manager.RespawnManager.respawnGameMode
import it.forgottenworld.dungeons.manager.RespawnManager.respawnLocation
import it.forgottenworld.dungeons.model.combat.InstanceObjective
import it.forgottenworld.dungeons.model.combat.MobSpawnData
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.*
import kotlinx.coroutines.delay
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

class DungeonFinalInstance(
        override val id: Int,
        override val dungeon: FinalDungeon,
        override val origin: BlockVector,
        override val triggers: Map<Int, Trigger>,
        override val activeAreas: Map<Int, ActiveArea>) : DungeonInstance {

    companion object {
        fun fromConfig(config: ConfigurationSection): DungeonFinalInstance? {
            val dungeon = DungeonManager.dungeons[config.getInt("dungeon_id")] ?: return null
            val instOriginBlock = ConfigManager.dungeonWorld.getBlockAt(
                    config.getDouble("x").toInt(),
                    config.getDouble("y").toInt(),
                    config.getDouble("z").toInt()
            )

            return dungeon.createInstance(instOriginBlock, config.getInt("instance_id"))
        }
    }

    var isTpSafe = true
    val players: MutableList<Player> = mutableListOf()
    var leader: Player? = null
    val minPlayers = dungeon.numberOfPlayers.first
    val maxPlayers = dungeon.numberOfPlayers.last
    var isLocked = false

    override val box = dungeon.box.withOrigin(origin)

    private val startingPostion = dungeon
            .startingLocation
            .withRefSystemOrigin(BlockVector(0, 0, 0), origin)

    fun resetInstance() {
        disbandParty()
        isTpSafe = true
        triggers.values.forEach {
            it.procced = false
            it.clearCurrentlyInsidePlayers()
        }
        activeAreas.values.forEach { it.fillWithMaterial(it.startingMaterial) }
        instanceObjectives.forEach { it.abort() }
        inGame = false
    }

    var inGame = false
    var partyKey = ""

    val playerCount: Int
        get() = players.size

    val isFull: Boolean
        get() = playerCount == maxPlayers

    fun lock() {
        isLocked = true
        partyKey = getRandomString(10)
    }

    fun unlock() {
        isLocked = false
        partyKey = ""
    }

    private fun disbandParty() {
        players.forEach { it.run {
            returnPosition = null
            returnGameMode = null
            collidingTrigger = null
            dungeonInstance = null
        } }
        players.clear()
    }

    private fun checkUpdateLeader(player: Player) {
        if (leader != player) return
        if (players.isEmpty()) resetInstance()
        else leader = players.first().apply { sendFWDMessage("You're now the party leader") }
    }

    fun onPlayerJoin(player: Player) {
        if (isFull) {
            player.sendFWDMessage("This dungeon party is full")
            return
        }

        if (inGame) {
            player.sendFWDMessage("This dungeon party has already entered the dungeon")
            return
        }

        if (players.isEmpty()) {
            leader = player
            player.spigot()
                    .sendMessage(*component {
                        append("${getString(Strings.CHAT_PREFIX)}Dungeon party created. To make it private, click ")
                        append(getLockClickable())
                    })
        } else {
            player.sendFWDMessage("You joined the dungeon party")
            players.forEach { it.sendFWDMessage("${player.name} joined the dungeon party") }
        }

        players.add(player)
        player.dungeonInstance = this
        return
    }

    private fun onPlayerRemoved(player: Player) {
        players.remove(player.apply {
            returnPosition = null
            returnGameMode = null
            collidingTrigger?.onPlayerExit(player)
            dungeonInstance = null
        })
        checkUpdateLeader(player)
    }

    fun onPlayerLeave(player: Player) {
        if (inGame) {
            player.health = 0.0
            return
        }
        players.forEach {
            it.sendFWDMessage("${if (player.name == it.name) "You" else player.name} left the dungeon party")
        }
        onPlayerRemoved(player)
    }

    fun onPlayerDeath(player: Player) {
        players.forEach { it.sendFWDMessage("${player.name} died in the dungeon") }
        player.respawnLocation = player.returnPosition
        player.respawnGameMode = player.returnGameMode
        onPlayerRemoved(player)
    }

    fun onStart() {
        // triggers.values.forEach { it.applyMeta() }
        inGame = true
        players.runForEach {
            returnPosition = location.clone()
            returnGameMode = gameMode
            gameMode = GameMode.ADVENTURE
            val startingLocation = startingPostion.locationInWorld(ConfigManager.dungeonWorld)
            teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            sendFWDMessage("Good luck out there!")
        }
        isTpSafe = false
        startCheckingTriggers()
    }
    
    fun onInstanceFinish(givePoints: Boolean) {
        if (givePoints && dungeon.points != 0)
            players
                    .map { it.uniqueId }
                    .let { DungeonCompletedEvent(it, dungeon.points.toFloat()) }
                    .let { Bukkit.getPluginManager().callEvent(it) }

        isTpSafe = true

        players.forEach {
            it.sendFWDMessage("${ChatColor.GREEN}Congratulations, you made it out alive!")
            it.returnPosition?.let { pos -> it.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN) }
            it.returnGameMode?.let { gm -> it.gameMode = gm }
        }

        resetInstance()
    }

    fun evacuate(): Boolean {
        onInstanceFinish(false)
        return true
    }

    private fun checkTriggers(
            playerUuid: UUID,
            posVector: Vector,
            oldTriggerId: Int?
    ) = launchAsync {
        val triggerId = triggers.values.find {
            it.containsVector(posVector)
        }?.id

        if (oldTriggerId == triggerId) return@launchAsync

        launch {
            Bukkit.getPluginManager().callEvent(TriggerEvent(
                    playerUuid,
                    triggerId ?: -1,
                    oldTriggerId != null
            ))
        }
    }

    private fun startCheckingTriggers() = launch {
        while (inGame) {
            delay(500)
            players.forEach {
                checkTriggers(
                        it.uniqueId,
                        it.location.toVector(),
                        it.collidingTrigger?.id)
            }
        }
    }

    val instanceObjectives = mutableListOf<InstanceObjective>()

    fun attachNewObjective(
            mobs: List<MobSpawnData>,
            onAllKilled: (DungeonFinalInstance) -> Unit) {
        val mobUuids = mobs.mapNotNull {
            spawnMob(it.isMythic,
                    it.mob,
                    (activeAreas[it.activeAreaId] ?: error("Active area not found")).getRandomLocationOnFloor()
                            .clone()
                            .add(0.5, 0.5, 0.5)
            )
        }.toMutableList()

        val obj = InstanceObjective(this, mobUuids, onAllKilled)
        mobUuids.forEach { InstanceObjectiveManager.entityObjectives[it] = obj }
        instanceObjectives.add(obj)
    }

    private fun spawnMob(isMythic: Boolean, type: String, location: Location) =
            if (isMythic) spawnMythicMob(type, location)
            else spawnVanillaMob(type, location)

    private fun spawnMythicMob(type: String, location: Location) =
            BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnVanillaMob(type: String, location: Location) =
            location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId

    fun toConfig(config: ConfigurationSection) {
        config.run {
            set("dungeon_id", dungeon.id)
            set("instance_id", id)
            set("x", origin.x)
            set("y", origin.y)
            set("z", origin.z)
        }
    }
}