package it.forgottenworld.dungeons.core.game.dungeon.instance

import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import it.forgottenworld.dungeons.api.game.objective.MobSpawnData
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.cli.JsonMessageGenerator
import it.forgottenworld.dungeons.core.storage.Configuration
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.game.detection.TriggerChecker
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveFactory
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager
import it.forgottenworld.dungeons.core.game.respawn.RespawnData
import it.forgottenworld.dungeons.core.game.respawn.RespawnData.Companion.currentRespawnData
import it.forgottenworld.dungeons.core.game.respawn.RespawnManager
import it.forgottenworld.dungeons.core.integrations.EasyRankingUtils
import it.forgottenworld.dungeons.core.integrations.FWEchelonUtils
import it.forgottenworld.dungeons.core.utils.*
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class DungeonInstanceImpl @AssistedInject constructor(
    @Assisted override val id: Int,
    @Assisted override val dungeon: FinalDungeon,
    @Assisted override val origin: Vector3i,
    private val configuration: Configuration,
    private val easyRankingUtils: EasyRankingUtils,
    private val fwEchelonUtils: FWEchelonUtils,
    private val jsonMessageGenerator: JsonMessageGenerator,
    private val triggerChecker: TriggerChecker,
    private val randomStringGenerator: RandomStringGenerator,
    private val combatObjectiveFactory: CombatObjectiveFactory,
    private val combatObjectiveManager: CombatObjectiveManager,
    private val respawnManager: RespawnManager,
    private val dungeonManager: DungeonManager,
    private val mythicMobsHelper: BukkitAPIHelper
) : DungeonInstance, Storage.Storable {

    @AssistedInject
    constructor(
        @Assisted dungeon: FinalDungeon,
        @Assisted origin: Vector3i,
        configuration: Configuration,
        easyRankingUtils: EasyRankingUtils,
        fwEchelonUtils: FWEchelonUtils,
        jsonMessageGenerator: JsonMessageGenerator,
        triggerChecker: TriggerChecker,
        randomStringGenerator: RandomStringGenerator,
        combatObjectiveFactory: CombatObjectiveFactory,
        combatObjectiveManager: CombatObjectiveManager,
        respawnManager: RespawnManager,
        dungeonManager: DungeonManager,
        mythicMobsHelper: BukkitAPIHelper
    ) : this(
        dungeonManager.getDungeonInstances(dungeon).keys.firstGap(),
        dungeon,
        origin,
        configuration,
        easyRankingUtils,
        fwEchelonUtils,
        jsonMessageGenerator,
        triggerChecker,
        randomStringGenerator,
        combatObjectiveFactory,
        combatObjectiveManager,
        respawnManager,
        dungeonManager,
        mythicMobsHelper
    )

    init {
        dungeonManager.registerDungeonInstance(this)
        resetActiveAreas()
    }

    override var leader: UUID? = null

    override var partyKey = ""

    override val instanceObjectives = mutableListOf<CombatObjective>()

    override val players = mutableListOf<UUID>()

    override var isLocked = false
        private set

    override var isInGame = false
        private set

    override var isTpSafe = true
        private set

    private val playerTriggers = mutableMapOf<UUID, Int>()

    private val proccedTriggers = mutableSetOf<Int>()

    private val playerRespawnData = mutableMapOf<UUID, RespawnData>()

    private val startingPostion = dungeon
        .startingLocation
        .withRefSystemOrigin(Vector3i.ZERO, origin)

    private fun resetActiveAreas() {
        for (aa in dungeon.activeAreas.values) {
            aa.fillWithMaterial(aa.startingMaterial, this)
        }
    }

    private fun resetInstance() {
        players.forEach { uuid ->
            playerRespawnData.remove(uuid)
            Bukkit.getPlayer(uuid)?.let { fwEchelonUtils.playerIsNowFree(it) }
            dungeonManager.setPlayerInstance(uuid, null)
        }
        players.clear()

        unlock()

        leader = null
        isTpSafe = true

        playerTriggers.clear()
        proccedTriggers.clear()

        resetActiveAreas()

        for (io in instanceObjectives) {
            io.abort()
        }
        instanceObjectives.clear()

        for (c in dungeon.chests.values) {
            c.clearActualChest(
                configuration.dungeonWorld,
                c.position.withRefSystemOrigin(Vector3i.ZERO, origin)
            )
        }

        val boundingBox = dungeon.box.getBoundingBox(origin)

        configuration.dungeonWorld
            .getNearbyEntities(boundingBox)
            .filter { it is LivingEntity && it !is Player }
            .forEach { (it as LivingEntity).health = 0.0 }

        launch {
            delay(500)
            configuration.dungeonWorld
                .getNearbyEntities(boundingBox)
                .filterIsInstance<Item>()
                .forEach { it.remove() }
            isInGame = false
        }
    }

    override fun lock() {
        isLocked = true
        partyKey = randomStringGenerator.generate(10)
    }

    override fun unlock() {
        isLocked = false
        partyKey = ""
    }

    override fun onPlayerJoin(player: Player) {
        if (!fwEchelonUtils.isPlayerFree(player)) {
            player.sendPrefixedMessage(Strings.YOU_CANNOT_JOIN_A_DUNGEON_RIGHT_NOW)
            return
        }

        if (players.size == dungeon.maxPlayers) {
            player.sendPrefixedMessage(Strings.DUNGEON_PARTY_IS_FULL)
            return
        }

        if (isInGame) {
            player.sendPrefixedMessage(Strings.PARTY_HAS_ALREADY_ENTERED_DUNGEON)
            return
        }

        fwEchelonUtils.playerIsNoLongerFree(player)

        if (players.isEmpty()) {
            leader = player.uniqueId
            player.sendJsonMessage {
                +"${Strings.CHAT_PREFIX}${Strings.DUNGEON_PARTY_CREATED_TO_CLOSE_CLICK} "
                +jsonMessageGenerator.lockLink
            }
        } else {
            player.sendPrefixedMessage(Strings.YOU_JOINED_DUNGEON_PARTY)
            players.forEach {
                Bukkit.getPlayer(it)?.sendPrefixedMessage(
                    Strings.PLAYER_JOINED_DUNGEON_PARTY,
                    player.name
                )
            }
        }

        players.add(player.uniqueId)
        dungeonManager.setPlayerInstance(player.uniqueId, this)
        return
    }

    override fun onStart() {
        isInGame = true
        players.forEach { Bukkit.getPlayer(it)?.let(::preparePlayer) }
        for (c in dungeon.chests.values) {
            c.fillActualChest(
                configuration.dungeonWorld,
                c.position.withRefSystemOrigin(Vector3i.ZERO, origin)
            )
        }
        isTpSafe = false
    }

    private fun preparePlayer(player: Player) {
        playerRespawnData[player.uniqueId] = player.currentRespawnData
        player.gameMode = GameMode.ADVENTURE
        val startingLocation = startingPostion.locationInWorld(configuration.dungeonWorld)
        player.teleport(startingLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
        player.sendPrefixedMessage(Strings.GOOD_LUCK_OUT_THERE)
    }

    private fun onTriggerProc(trigger: Trigger) {
        if (proccedTriggers.contains(trigger.id) ||
            trigger.requiresWholeParty &&
            playerTriggers.values.count { it == trigger.id } != players.size
        ) return
        proccedTriggers.add(trigger.id)
        trigger.executeEffect(this)
    }

    private fun onPlayerEnterTrigger(player: Player, trigger: Trigger) {
        if (configuration.debugMode) {
            trigger.debugLogEnter(player)
        }
        onTriggerProc(trigger)
    }

    private fun onPlayerExitTrigger(player: Player, trigger: Trigger) {
        if (configuration.debugMode) {
            trigger.debugLogExit(player)
        }
    }

    private fun onPlayerRemoved(player: Player) {
        playerRespawnData.remove(player.uniqueId)
        playerTriggers[player.uniqueId]
            ?.let { dungeon.triggers[it] }
            ?.let { onPlayerExitTrigger(player, it) }
        playerTriggers.remove(player.uniqueId)
        dungeonManager.setPlayerInstance(player.uniqueId, null)
        players.remove(player.uniqueId)
        fwEchelonUtils.playerIsNowFree(player)
        updatePartyLeader(player)
    }

    private fun updatePartyLeader(player: Player) {
        if (leader != player.uniqueId) return
        if (players.isEmpty()) {
            resetInstance()
        } else {
            leader = players.first()
            Bukkit.getPlayer(leader!!)?.sendPrefixedMessage(Strings.NOW_PARTY_LEADER)
        }
    }

    override fun onPlayerLeave(player: Player) {
        if (isInGame) {
            player.health = 0.0
            return
        }
        for (uuid in players) {
            val pl = Bukkit.getPlayer(uuid) ?: continue
            val token = if (player.name == pl.name) Strings.YOU else player.name
            pl.sendPrefixedMessage(Strings.PLAYER_LEFT_DUNGEON_PARTY, token)
        }
        onPlayerRemoved(player)
    }

    override fun onPlayerDeath(player: Player) {
        player.sendPrefixedMessage(Strings.YOU_DIED_IN_THE_DUNGEON)
        respawnManager.setPlayerRespawnData(player.uniqueId, playerRespawnData[player.uniqueId])
        onPlayerRemoved(player)
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendPrefixedMessage(
                Strings.PLAYER_DIED_IN_DUNGEON,
                player.name
            )
        }
    }

    override fun onFinishTriggered() {
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendPrefixedMessage(Strings.YOU_WILL_EXIT_THE_DUNGEON_IN_5_SECS)
        }
        launch {
            delay(5000)
            onInstanceFinish(true)
        }
    }

    override fun onInstanceFinish(givePoints: Boolean) {
        if (givePoints && dungeon.points != 0) {
            for (uuid in players) {
                easyRankingUtils.addScoreToPlayer(uuid, dungeon.points.toFloat())
            }
        }

        isTpSafe = true

        for (uuid in players) {
            val pl = Bukkit.getPlayer(uuid) ?: continue
            pl.sendPrefixedMessage(Strings.CONGRATS_YOU_MADE_IT_OUT)
            playerRespawnData[uuid]?.useWithPlayer(pl)
        }

        resetInstance()
    }

    override fun rescuePlayer(player: Player) {
        playerRespawnData[player.uniqueId]?.useWithPlayer(player)
        onPlayerRemoved(player)
    }

    override fun evacuate(): Boolean {
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
        val newTrigger = triggerChecker.checkPositionAgainstTriggers(
            dungeon.triggerGrid,
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

    override fun onPlayerMove(player: Player) {
        if (!isInGame) return
        val loc = player.location
        val oldTrigger = playerTriggers[player.uniqueId]
            ?.let(dungeon.triggers::get)
        checkTriggers(
            player,
            loc.blockX,
            loc.blockY,
            loc.blockZ,
            oldTrigger
        )
    }

    override fun attachNewObjective(
        mobs: List<MobSpawnData>,
        onAllKilled: (DungeonInstance) -> Unit
    ) {
        val mobUuids = mobs.mapNotNull {
            val sa = dungeon.spawnAreas[it.spawnAreaId] ?: error("Spawn area not found")
            spawnMob(it.isMythic, it.mob, sa.getRandomLocationOnFloor(this))
        }.toMutableList()
        val obj = combatObjectiveFactory.create(this, mobUuids, onAllKilled)
        mobUuids.forEach {
            combatObjectiveManager.setEntityCombatObjective(it, obj)
        }
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
}