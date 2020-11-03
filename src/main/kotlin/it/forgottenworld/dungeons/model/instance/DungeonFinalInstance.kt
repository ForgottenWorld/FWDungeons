package it.forgottenworld.dungeons.model.instance

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.cli.Strings
import it.forgottenworld.dungeons.cli.getLockClickable
import it.forgottenworld.dungeons.cli.getString
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.event.listener.RespawnHandler.Companion.respawnData
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler.Companion.collidingTrigger
import it.forgottenworld.dungeons.model.combat.CombatObjective
import it.forgottenworld.dungeons.model.combat.CombatObjective.Companion.combatObjective
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.dungeon.finalDungeons
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.model.interactiveelement.instanceActiveAreas
import it.forgottenworld.dungeons.model.interactiveelement.instanceTriggers
import it.forgottenworld.dungeons.utils.*
import it.forgottenworld.dungeons.utils.ktx.*
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
        dungeonId: Int,
        override val origin: BlockVector) : DungeonInstance {

    override val dungeon by finalDungeons(dungeonId)
    override val box = dungeon.box.withOrigin(origin)
    override val triggers by instanceTriggers()
    val unproccedTriggers = mutableListOf<Trigger>()
    override val activeAreas by instanceActiveAreas()

    var isTpSafe = true
    val players = mutablePlayerListOf()
    var leader by safePlayer()
    val minPlayers = dungeon.numberOfPlayers.first
    val maxPlayers = dungeon.numberOfPlayers.last
    var isLocked = false
    var inGame = false
    var partyKey = ""
    val instanceObjectives = mutableListOf<CombatObjective>()

    private val warpbackData = mutableMapOf<UUID, WarpbackData>()

    private val startingPostion = dungeon
            .startingLocation
            .withRefSystemOrigin(BlockVector(0, 0, 0), origin)

    val playerCount
        get() = players.size

    val isFull
        get() = playerCount == maxPlayers

    fun resetInstance() {
        players.uuids.forEach { it.run {
            warpbackData.remove(this)
            collidingTrigger = null
            finalInstance = null
        } }
        unlock()
        leader = null
        players.clear()
        isTpSafe = true
        unproccedTriggers.clear()
        unproccedTriggers.addAll(triggers.values)
        triggers.values.forEach {
            it.procced = false
            it.clearCurrentlyInsidePlayers()
        }
        activeAreas.values.forEach { it.fillWithMaterial(it.startingMaterial) }
        instanceObjectives.forEach { it.abort() }
        inGame = false
    }


    fun lock() {
        isLocked = true
        partyKey = getRandomString(10)
    }

    fun unlock() {
        isLocked = false
        partyKey = ""
    }

    private fun checkUpdateLeader(player: Player) {
        if (leader != player) return
        if (players.isEmpty()) resetInstance()
        else leader = players.first()?.apply { sendFWDMessage("You're now the party leader") }
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
            players.forEach { it?.sendFWDMessage("${player.name} joined the dungeon party") }
        }

        players.add(player)
        player.finalInstance = this
        return
    }

    private fun onPlayerRemoved(player: Player) {
        players.remove(player.apply {
            warpbackData.remove(uniqueId)
            collidingTrigger?.onPlayerExit(player)
            finalInstance = null
        })
        checkUpdateLeader(player)
    }

    fun onPlayerLeave(player: Player) {
        if (inGame) {
            player.health = 0.0
            return
        }
        players.forEach {
            it?.sendFWDMessage("${if (player.name == it.name) "You" else player.name} left the dungeon party")
        }
        onPlayerRemoved(player)
    }

    fun onPlayerDeath(player: Player) {
        players.forEach { it?.sendFWDMessage("${player.name} died in the dungeon") }
        player.respawnData = warpbackData[player.uniqueId]
        onPlayerRemoved(player)
    }

    fun onStart() {
        inGame = true
        players.forEach { it?.run {
            warpbackData[it.uniqueId] = WarpbackData(gameMode, location.toVector())
            gameMode = GameMode.ADVENTURE
            val startingLocation = startingPostion.locationInWorld(ConfigManager.dungeonWorld)
            teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            sendFWDMessage("Good luck out there!")
        } }
        isTpSafe = false
        startCheckingTriggers()
    }

    fun onInstanceFinish(givePoints: Boolean) {

        if (givePoints && dungeon.points != 0) players
                .mapNotNull { it?.uniqueId }
                .let { DungeonCompletedEvent(it, dungeon.points.toFloat()) }
                .let { Bukkit.getPluginManager().callEvent(it) }

        isTpSafe = true

        players.forEach { p -> p?.run {
            sendFWDMessage("${ChatColor.GREEN}Congratulations, you made it out alive!")
            warpbackData[uniqueId]?.let {
                teleport(it.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                gameMode = it.gameMode
            }
         } }

        warpbackData.clear()

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
        val triggerId = unproccedTriggers.find {
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
            players.forEach { p -> p?.run {
                checkTriggers(
                        uniqueId,
                        location.toVector(),
                        collidingTrigger?.id)
            } }
        }
    }

    fun attachNewObjective(
            mobs: List<MobSpawnData>,
            onAllKilled: (DungeonFinalInstance) -> Unit) {
        val mobUuids = mobs.mapNotNull {
            spawnMob(it.isMythic,
                    it.mob,
                    (activeAreas[it.activeAreaId] ?: error("Active area not found"))
                            .getRandomLocationOnFloor()
                            .clone()
                            .add(0.5, 0.5, 0.5)
            )
        }.toMutableList()

        val obj = CombatObjective(this, mobUuids, onAllKilled)
        mobUuids.forEach { it.combatObjective = obj }
        instanceObjectives.add(obj)
    }

    private fun spawnMob(isMythic: Boolean, type: String, location: Location) =
            if (isMythic) spawnMythicMob(type, location)
            else spawnVanillaMob(type, location)

    private fun spawnMythicMob(type: String, location: Location) =
            BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnVanillaMob(type: String, location: Location) =
            location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId

    companion object {

        val finalInstances = mutableMapOf<UUID, DungeonFinalInstance>()

        var Player.finalInstance
            get() = finalInstances[uniqueId]
            set(value) {
                value?.let {
                    finalInstances[uniqueId] = value
                } ?: finalInstances.remove(uniqueId)
            }

        var UUID.finalInstance
            get() = finalInstances[this]
            set(value) {
                value?.let {
                    finalInstances[this] = value
                } ?: finalInstances.remove(this)
            }

        fun fromConfig(dungeonId: Int, config: ConfigurationSection): DungeonFinalInstance? {
            val dungeon = FinalDungeon.dungeons[dungeonId] ?: return null
            val instOriginBlock = ConfigManager.dungeonWorld.getBlockAt(
                    config.getInt("x"),
                    config.getInt("y"),
                    config.getInt("z")
            )

            return dungeon.createInstance(instOriginBlock)
        }
    }
}