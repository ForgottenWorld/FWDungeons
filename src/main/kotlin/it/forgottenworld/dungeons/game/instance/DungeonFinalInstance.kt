package it.forgottenworld.dungeons.game.instance

import it.forgottenworld.dungeons.cli.JsonMessages
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.detection.CubeGridUtils.checkPositionAgainstTriggers
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon.FinalDungeonsDelegate.Companion.finalDungeons
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea.FinalInstanceActiveAreaDelegate.Companion.instanceActiveAreas
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.game.interactiveregion.Trigger.ActivationHandler.Companion.collidingTrigger
import it.forgottenworld.dungeons.game.interactiveregion.Trigger.FinalInstanceTriggerDelegate.Companion.instanceTriggers
import it.forgottenworld.dungeons.game.objective.CombatObjective
import it.forgottenworld.dungeons.game.objective.CombatObjective.Companion.combatObjective
import it.forgottenworld.dungeons.listener.RespawnHandler.Companion.respawnData
import it.forgottenworld.dungeons.utils.EasyRankingUtils
import it.forgottenworld.dungeons.utils.MobSpawnData
import it.forgottenworld.dungeons.utils.MutablePlayerList
import it.forgottenworld.dungeons.utils.RandomString
import it.forgottenworld.dungeons.utils.Vector3i
import it.forgottenworld.dungeons.utils.WarpbackData
import it.forgottenworld.dungeons.utils.WarpbackData.Companion.currentWarpbackData
import it.forgottenworld.dungeons.utils.dungeonWorld
import it.forgottenworld.dungeons.utils.launch
import it.forgottenworld.dungeons.utils.locationInWorld
import it.forgottenworld.dungeons.utils.mythicMobsHelper
import it.forgottenworld.dungeons.utils.player
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.sendJsonMessage
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
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

class DungeonFinalInstance(
    override val id: Int,
    dungeonId: Int,
    override val origin: Vector3i
) : DungeonInstance {

    override val dungeon by finalDungeons(dungeonId)
    override val box = dungeon.box.withOrigin(origin)
    override val triggers by instanceTriggers()
    override val activeAreas by instanceActiveAreas()

    var isTpSafe = true
    val players = MutablePlayerList.of()
    var leader by player()
    val minPlayers = dungeon.numberOfPlayers.first
    val maxPlayers = dungeon.numberOfPlayers.last
    var isLocked = false
    var inGame = false
    var partyKey = ""
    val instanceObjectives = mutableListOf<CombatObjective>()

    private val warpbackData = mutableMapOf<UUID, WarpbackData>()

    private val startingPostion = dungeon
        .startingLocation
        .withRefSystemOrigin(Vector3i(0, 0, 0), origin)

    val playerCount
        get() = players.size

    val isFull
        get() = playerCount == maxPlayers

    fun resetInstance() {
        players.uuids.forEach {
            it.run {
                warpbackData.remove(this)
                finalInstance = null
            }
        }
        players.clear()
        unlock()
        leader = null
        isTpSafe = true
        triggers.values.forEach { it.reset() }
        activeAreas.values.forEach { it.fillWithMaterial(it.startingMaterial) }
        instanceObjectives.forEach { it.abort() }
        instanceObjectives.clear()
        dungeonWorld
            .getNearbyEntities(box.boundingBox)
            .filter { it is LivingEntity && it !is Player }
            .forEach { (it as LivingEntity).health = 0.0 }
        launch {
            delay(500)
            dungeonWorld
                .getNearbyEntities(box.boundingBox)
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

    private fun checkUpdateLeader(player: Player) {
        if (leader != player) return
        if (players.isEmpty()) resetInstance()
        else leader = players.first()?.apply { sendFWDMessage(Strings.NOT_PARTY_LEADER) }
    }

    fun onPlayerJoin(player: Player) {
        if (isFull) {
            player.sendFWDMessage(Strings.DUNGEON_PARTY_IS_FULL)
            return
        }

        if (inGame) {
            player.sendFWDMessage(Strings.PARTY_HAS_ALREADY_ENTERED_DUNGEON)
            return
        }

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

    private fun onPlayerRemoved(player: Player) {
        warpbackData.remove(player.uniqueId)
        player.collidingTrigger?.onPlayerExit(player)
        player.finalInstance = null
        players.remove(player)
        checkUpdateLeader(player)
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
        players.forEach { it?.sendFWDMessage(Strings.PLAYER_DIED_IN_DUNGEON.format(player.name)) }
        player.respawnData = warpbackData[player.uniqueId]
        onPlayerRemoved(player)
    }

    private fun preparePlayer(player: Player) {
        warpbackData[player.uniqueId] = player.currentWarpbackData
        player.gameMode = GameMode.ADVENTURE
        val startingLocation = startingPostion.locationInWorld(ConfigManager.dungeonWorld)
        player.teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
        player.sendFWDMessage(Strings.GOOD_LUCK_OUT_THERE)
    }

    fun onStart() {
        inGame = true
        players.filterNotNull().forEach { preparePlayer(it) }
        isTpSafe = false
        startCheckingTriggers()
    }

    fun onInstanceFinish(givePoints: Boolean) {

        if (ConfigManager.useEasyRanking && givePoints && dungeon.points != 0) {
            players.mapNotNull { it?.uniqueId }.forEach {
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

    fun evacuate(): Boolean {
        onInstanceFinish(false)
        return true
    }

    private fun checkTriggers(
        playerUuid: UUID,
        x: Int,
        y: Int,
        z: Int,
        oldTriggerId: Int?
    ) {
        val triggerId = dungeon
            .triggerGrid
            .checkPositionAgainstTriggers(
                x - origin.x,
                y - origin.y,
                z - origin.z,
                triggers
            )

        if (oldTriggerId != triggerId) {
            val event = Trigger.Event(playerUuid, triggerId ?: -1, oldTriggerId != null)
            Bukkit.getPluginManager().callEvent(event)
        }
    }

    private fun startCheckingTriggers() = launch {
        while (inGame) {
            delay(500)
            players.forEach {
                if (it == null) return@forEach
                val loc = it.location
                checkTriggers(
                    it.uniqueId,
                    loc.blockX,
                    loc.blockY,
                    loc.blockZ,
                    it.collidingTrigger?.id
                )
            }
        }
    }

    fun attachNewObjective(
        mobs: List<MobSpawnData>,
        onAllKilled: (DungeonFinalInstance) -> Unit
    ) {
        val mobUuids = mobs.mapNotNull {
            spawnMob(
                it.isMythic,
                it.mob,
                (activeAreas[it.activeAreaId] ?: error("Active area not found"))
                    .getRandomLocationOnFloor()
            )
        }.toMutableList()

        val obj = CombatObjective(this, mobUuids, onAllKilled)
        mobUuids.forEach { it.combatObjective = obj }
        instanceObjectives.add(obj)
    }

    private fun spawnMob(isMythic: Boolean, type: String, location: Location) =
        if (isMythic) spawnMythicMob(type, location)
        else spawnVanillaMob(type, location)

    private fun spawnMythicMob(type: String, location: Location) = mythicMobsHelper
        .spawnMythicMob(type, location).uniqueId

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