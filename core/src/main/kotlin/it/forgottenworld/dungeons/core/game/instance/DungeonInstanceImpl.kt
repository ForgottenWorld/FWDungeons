package it.forgottenworld.dungeons.core.game.instance

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.cli.JsonMessages
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.RespawnManager.respawnData
import it.forgottenworld.dungeons.core.game.detection.CubeGridFactory.checkPositionAgainstTriggers
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.game.objective.CombatObjective
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager.combatObjective
import it.forgottenworld.dungeons.core.game.objective.MobSpawnData
import it.forgottenworld.dungeons.core.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.core.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.core.utils.*
import it.forgottenworld.dungeons.core.utils.WarpbackData.Companion.currentWarpbackData
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class DungeonInstanceImpl(
    override val id: Int,
    override val dungeon: FinalDungeon,
    override val origin: Vector3i
) : DungeonInstance {

    var isTpSafe = true
    val players = mutableListOf<UUID>()
    var leader: UUID? = null
    var isLocked = false
    var inGame = false
    var partyKey = ""
    val instanceObjectives = mutableListOf<CombatObjective>()

    val playerTriggers = mutableMapOf<UUID, Int>()
    val proccedTriggers = mutableSetOf<Int>()

    private val warpbackData = mutableMapOf<UUID, WarpbackData>()

    private val startingPostion = dungeon
        .startingLocation
        .withRefSystemOrigin(Vector3i.ZERO, origin)

    val playerCount
        get() = players.size

    val isFull
        get() = playerCount == dungeon.maxPlayers

    fun resetInstance() {
        players.forEach { uuid ->
            warpbackData.remove(uuid)
            Bukkit.getPlayer(uuid)?.let { FWEchelonUtils.playerIsNowFree(it) }
            uuid.finalInstance = null
        }
        players.clear()
        unlock()
        leader = null
        isTpSafe = true
        playerTriggers.clear()
        proccedTriggers.clear()
        for (aa in dungeon.activeAreas.values) {
            aa.fillWithMaterial(aa.startingMaterial, this)
        }
        for (io in instanceObjectives) {
            io.abort()
        }
        for (c in dungeon.chests.values) {
            c.clearActualChest(
                Configuration.dungeonWorld,
                c.position.withRefSystemOrigin(Vector3i.ZERO, origin)
            )
        }
        instanceObjectives.clear()
        val boundingBox = dungeon.box.getBoundingBox(origin)
        Configuration.dungeonWorld
            .getNearbyEntities(boundingBox)
            .filter { it is LivingEntity && it !is Player }
            .forEach { (it as LivingEntity).health = 0.0 }
        launch {
            delay(500)
            Configuration.dungeonWorld
                .getNearbyEntities(boundingBox)
                .filterIsInstance<Item>()
                .forEach { it.remove() }
            inGame = false
        }
    }

    fun lock() {
        isLocked = true
        partyKey = RandomString.generate(10)
    }

    fun unlock() {
        isLocked = false
        partyKey = ""
    }

    fun onPlayerJoin(player: Player) {
        if (!FWEchelonUtils.isPlayerFree(player)) {
            player.sendFWDMessage(Strings.YOU_CANNOT_JOIN_A_DUNGEON_RIGHT_NOW)
            return
        }

        if (isFull) {
            player.sendFWDMessage(Strings.DUNGEON_PARTY_IS_FULL)
            return
        }

        if (inGame) {
            player.sendFWDMessage(Strings.PARTY_HAS_ALREADY_ENTERED_DUNGEON)
            return
        }

        FWEchelonUtils.playerIsNoLongerFree(player)

        if (players.isEmpty()) {
            leader = player.uniqueId
            player.sendJsonMessage {
                append("${Strings.CHAT_PREFIX}${Strings.DUNGEON_PARTY_CREATED_TO_CLOSE_CLICK} ")
                append(JsonMessages.lockLink())
            }
        } else {
            player.sendFWDMessage(Strings.YOU_JOINED_DUNGEON_PARTY)
            players.forEach {
                Bukkit.getPlayer(it)?.sendFWDMessage(
                    Strings.PLAYER_JOINED_DUNGEON_PARTY.format(player.name)
                )
            }
        }

        players.add(player.uniqueId)
        player.uniqueId.finalInstance = this
        return
    }

    fun onStart() {
        inGame = true
        players.forEach { Bukkit.getPlayer(it)?.let(::preparePlayer) }
        for (c in dungeon.chests.values) {
            c.fillActualChest(
                Configuration.dungeonWorld,
                c.position.withRefSystemOrigin(Vector3i.ZERO, origin)
            )
        }
        isTpSafe = false
    }

    private fun preparePlayer(player: Player) {
        warpbackData[player.uniqueId] = player.currentWarpbackData
        player.gameMode = GameMode.ADVENTURE
        val startingLocation = startingPostion.locationInWorld(Configuration.dungeonWorld)
        player.teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
        player.sendFWDMessage(Strings.GOOD_LUCK_OUT_THERE)
    }

    private fun onPlayerEnterTrigger(player: Player, trigger: Trigger) {
        if (Configuration.isDebugMode) {
            (trigger as TriggerImpl).debugLogEnter(player)
        }
        trigger.proc(this)
    }

    private fun onPlayerExitTrigger(player: Player, trigger: Trigger) {
        if (Configuration.isDebugMode) {
            (trigger as TriggerImpl).debugLogExit(player)
        }
    }

    private fun onPlayerRemoved(player: Player) {
        warpbackData.remove(player.uniqueId)
        playerTriggers[player.uniqueId]
            ?.let { dungeon.triggers[it] }
            ?.let { onPlayerExitTrigger(player, it) }
        playerTriggers.remove(player.uniqueId)
        player.uniqueId.finalInstance = null
        players.remove(player.uniqueId)
        FWEchelonUtils.playerIsNowFree(player)
        updatePartyLeader(player)
    }

    private fun updatePartyLeader(player: Player) {
        if (leader != player.uniqueId) return
        if (players.isEmpty()) {
            resetInstance()
        } else {
            leader = players.first()
            Bukkit.getPlayer(leader!!)?.sendFWDMessage(Strings.NOW_PARTY_LEADER)
        }
    }

    fun onPlayerLeave(player: Player) {
        if (inGame) {
            player.health = 0.0
            return
        }
        for (uuid in players) {
            val pl = Bukkit.getPlayer(uuid) ?: continue
            val token = if (player.name == pl.name) Strings.YOU else player.name
            pl.sendFWDMessage(Strings.PLAYER_LEFT_DUNGEON_PARTY.format(token))
        }
        onPlayerRemoved(player)
    }

    fun onPlayerDeath(player: Player) {
        player.sendFWDMessage(Strings.YOU_DIED_IN_THE_DUNGEON)
        player.uniqueId.respawnData = warpbackData[player.uniqueId]
        onPlayerRemoved(player)
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendFWDMessage(
                Strings.PLAYER_DIED_IN_DUNGEON.format(player.name)
            )
        }
    }

    fun onInstanceFinish(givePoints: Boolean) {
        if (Configuration.useEasyRanking && givePoints && dungeon.points != 0) {
            for (uuid in players) {
                EasyRankingUtils.addScoreToPlayer(uuid, dungeon.points.toFloat())
            }
        }

        isTpSafe = true

        for (uuid in players) {
            val pl = Bukkit.getPlayer(uuid) ?: continue
            pl.sendFWDMessage(Strings.CONGRATS_YOU_MADE_IT_OUT)
            warpbackData[uuid]?.useWithPlayer(pl)
        }

        resetInstance()
    }

    fun rescuePlayer(player: Player) {
        warpbackData[player.uniqueId]?.useWithPlayer(player)
        onPlayerRemoved(player)
    }

    fun evacuate(): Boolean {
        onInstanceFinish(false)
        return true
    }

    private fun checkTriggers(
        player: Player,
        x: Int,
        y: Int,
        z: Int,
        oldTrigger: Trigger?
    ) {
        val newTrigger = dungeon
            .triggerGrid
            .checkPositionAgainstTriggers(
                x - origin.x,
                y - origin.y,
                z - origin.z,
                dungeon.triggers
            )

        if (oldTrigger?.id == newTrigger?.id) return
        oldTrigger?.let { onPlayerExitTrigger(player, it) }
        if (newTrigger != null) {
            onPlayerEnterTrigger(player, newTrigger)
            playerTriggers[player.uniqueId] = newTrigger.id
        } else {
            playerTriggers.remove(player.uniqueId)
        }
    }

    fun onPlayerMove(player: Player) {
        if (!inGame) return
        val loc = player.location
        val oldTrigger = playerTriggers[player.uniqueId]
            ?.let { dungeon.triggers[it] }
        checkTriggers(
            player,
            loc.blockX,
            loc.blockY,
            loc.blockZ,
            oldTrigger
        )
    }

    fun attachNewObjective(
        mobs: List<MobSpawnData>,
        onAllKilled: (DungeonInstanceImpl) -> Unit
    ) {
        val mobUuids = mobs.mapNotNull {
            val aa = dungeon.activeAreas[it.activeAreaId] ?: error("Active area not found")
            spawnMob(it.isMythic, it.mob, aa.getRandomLocationOnFloor(this))
        }.toMutableList()
        val obj = CombatObjective(this, mobUuids, onAllKilled)
        mobUuids.forEach { it.combatObjective = obj }
        instanceObjectives.add(obj)
    }

    private fun spawnMob(
        isMythic: Boolean,
        type: String,
        location: Location
    ) = if (isMythic) {
        spawnMythicMob(type, location)
    } else {
        spawnVanillaMob(type, location)
    }

    private fun spawnMythicMob(type: String, location: Location) = mythicMobsHelper
        .spawnMythicMob(type, location)
        .uniqueId

    private fun spawnVanillaMob(type: String, location: Location) = location
        .world
        ?.spawnEntity(location, EntityType.valueOf(type))
        ?.uniqueId

    companion object {

        private val mythicMobsHelper by lazy { BukkitAPIHelper() }

        fun fromConfig(dungeonId: Int, config: ConfigurationSection): DungeonInstanceImpl? {
            val dungeon = DungeonManager.finalDungeons[dungeonId] ?: return null
            val instOriginBlock = Configuration.dungeonWorld.getBlockAt(
                config.getInt("x"),
                config.getInt("y"),
                config.getInt("z")
            )

            return dungeon.createInstance(instOriginBlock)
        }
    }
}