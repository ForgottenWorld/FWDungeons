package it.forgottenworld.dungeons.core.game.instance

import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.math.locationInWorld
import it.forgottenworld.dungeons.api.math.withRefSystemOrigin
import it.forgottenworld.dungeons.core.cli.JsonMessages
import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.RespawnHandler.respawnData
import it.forgottenworld.dungeons.core.game.detection.CubeGridFactory.checkPositionAgainstTriggers
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.game.objective.CombatObjective
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager.combatObjective
import it.forgottenworld.dungeons.core.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.core.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.core.utils.MobSpawnData
import it.forgottenworld.dungeons.core.utils.MutablePlayerList
import it.forgottenworld.dungeons.core.utils.RandomString
import it.forgottenworld.dungeons.core.utils.WarpbackData
import it.forgottenworld.dungeons.core.utils.WarpbackData.Companion.currentWarpbackData
import it.forgottenworld.dungeons.core.utils.dungeonWorld
import it.forgottenworld.dungeons.core.utils.launch
import it.forgottenworld.dungeons.core.utils.mythicMobsHelper
import it.forgottenworld.dungeons.core.utils.player
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.dungeons.core.utils.sendJsonMessage
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
    val players = MutablePlayerList.of()
    var leader by player()
    var isLocked = false
    var inGame = false
    var partyKey = ""
    val instanceObjectives = mutableListOf<CombatObjective>()

    val playerTriggers = mutableMapOf<UUID,Int>()
    val proccedTriggers = mutableSetOf<Int>()

    private val warpbackData = mutableMapOf<UUID, WarpbackData>()

    private val startingPostion = dungeon
        .startingLocation
        .withRefSystemOrigin(Vector3i.ZERO, origin)

    val playerCount
        get() = players.size

    val isFull
        get() = playerCount == dungeon.numberOfPlayers.last

    fun resetInstance() {
        players.uuids.forEach { uuid ->
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
        instanceObjectives.clear()
        val boundingBox = dungeon.box.getBoundingBox(origin)
        dungeonWorld
            .getNearbyEntities(boundingBox)
            .filter { it is LivingEntity && it !is Player }
            .forEach { (it as LivingEntity).health = 0.0 }
        launch {
            delay(500)
            dungeonWorld
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
            leader = player
            player.sendJsonMessage {
                append("${Strings.CHAT_PREFIX}${Strings.DUNGEON_PARTY_CREATED_TO_CLOSE_CLICK} ")
                append(JsonMessages.lockLink())
            }
        } else {
            player.sendFWDMessage(Strings.YOU_JOINED_DUNGEON_PARTY)
            players.forEach {
                it?.sendFWDMessage(Strings.PLAYER_JOINED_DUNGEON_PARTY.format(player.name))
            }
        }

        players.add(player)
        player.finalInstance = this
        return
    }

    fun onStart() {
        inGame = true
        players.filterNotNull().forEach { preparePlayer(it) }
        isTpSafe = false
    }

    private fun preparePlayer(player: Player) {
        warpbackData[player.uniqueId] = player.currentWarpbackData
        player.gameMode = GameMode.ADVENTURE
        val startingLocation = startingPostion.locationInWorld(ConfigManager.dungeonWorld)
        player.teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
        player.sendFWDMessage(Strings.GOOD_LUCK_OUT_THERE)
    }

    private fun onPlayerEnterTrigger(player: Player, trigger: TriggerImpl) {
        if (ConfigManager.isDebugMode) trigger.debugLogEnter(player)
        trigger.proc(this)
    }

    private fun onPlayerExitTrigger(player: Player, trigger: TriggerImpl) {
        if (ConfigManager.isDebugMode) trigger.debugLogExit(player)
    }

    private fun onPlayerRemoved(player: Player) {
        warpbackData.remove(player.uniqueId)
        val trigId = playerTriggers[player.uniqueId]
        val trig = trigId?.let { dungeon.triggers[it] }
        trig?.let { onPlayerExitTrigger(player, it) }
        playerTriggers.remove(player.uniqueId)
        player.finalInstance = null
        players.remove(player)
        FWEchelonUtils.playerIsNowFree(player)
        updatePartyLeader(player)
    }

    private fun updatePartyLeader(player: Player) {
        if (leader != player) return
        if (players.isEmpty()) resetInstance()
        else leader = players.first()?.apply { sendFWDMessage(Strings.NOT_PARTY_LEADER) }
    }

    fun onPlayerLeave(player: Player) {
        if (inGame) {
            player.health = 0.0
            return
        }
        players.filterNotNull().forEach {
            val token = if (player.name == it.name) Strings.YOU else player.name
            it.sendFWDMessage(Strings.PLAYER_LEFT_DUNGEON_PARTY.format(token))
        }
        onPlayerRemoved(player)
    }

    fun onPlayerDeath(player: Player) {
        player.sendFWDMessage(Strings.YOU_DIED_IN_THE_DUNGEON)
        player.respawnData = warpbackData[player.uniqueId]
        onPlayerRemoved(player)
        players.forEach { it?.sendFWDMessage(Strings.PLAYER_DIED_IN_DUNGEON.format(player.name)) }
    }

    fun onInstanceFinish(givePoints: Boolean) {
        if (ConfigManager.useEasyRanking && givePoints && dungeon.points != 0) {
            players.uuids.forEach {
                EasyRankingUtils.addScoreToPlayer(it, dungeon.points.toFloat())
            }
        }

        isTpSafe = true

        players.filterNotNull().forEach { p ->
            p.sendFWDMessage(Strings.CONGRATS_YOU_MADE_IT_OUT)
            warpbackData[p.uniqueId]?.useWithPlayer(p)
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
        oldTrigger: TriggerImpl?
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

        fun fromConfig(dungeonId: Int, config: ConfigurationSection): DungeonInstanceImpl? {
            val dungeon = DungeonManager.finalDungeons[dungeonId] ?: return null
            val instOriginBlock = ConfigManager.dungeonWorld.getBlockAt(
                config.getInt("x"),
                config.getInt("y"),
                config.getInt("z")
            )

            return dungeon.createInstance(instOriginBlock)
        }
    }
}