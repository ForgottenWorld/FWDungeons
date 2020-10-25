package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.manager.DungeonManager.returnGameMode
import it.forgottenworld.dungeons.manager.DungeonManager.returnPosition
import it.forgottenworld.dungeons.manager.InstanceObjectiveManager
import it.forgottenworld.dungeons.utils.TypeWrapper
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        private val origin: BlockVector,
        val triggers: MutableMap<Int, Trigger>,
        val activeAreas: MutableList<ActiveArea>,
        val isTest: Boolean = false) {

    private val activeAreasIdMap = activeAreas.map { it.id to it }.toMap()
    var doHighlightFrames = TypeWrapper(false)
    private var activeAreaHlFrameLocs = TypeWrapper(setOf<Location>())
    private var triggerHlFrameLocs = TypeWrapper(setOf<Location>())
    var tester: Player? = null

    companion object {
        fun fromConfig(config: ConfigurationSection): DungeonInstance? {
            val dungeon = DungeonManager.dungeons[config.getInt("dungeon_id")] ?: return null
            val instOrigin = BlockVector(
                    config.getDouble("x"),
                    config.getDouble("y"),
                    config.getDouble("z"))

            val triggers = dungeon.triggers.map {
                Trigger(it.id,
                        dungeon,
                        it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                        it.effect,
                        it.requiresWholeParty
                ).apply { label = it.label }
            }.map { it.id to it }.toMap().toMutableMap()

            val activeAreas = dungeon.activeAreas.map {
                ActiveArea(it.id,
                        it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                        it.startingMaterial
                ).apply { label = it.label}
            }.toMutableList()

            return DungeonInstance(
                    config.getInt("instance_id"),
                    dungeon,
                    instOrigin,
                    triggers,
                    activeAreas
            ).apply {
                resetInstance()
            }
        }
    }

    fun getActiveAreaById(id: Int) = activeAreasIdMap[id]

    var party: Party? = null
    var isTpSafe = true

    val box
        get() = dungeon.box.withOrigin(origin)

    private val startingPostion = dungeon.startingLocation
            ?.withRefSystemOrigin(BlockVector(0,0,0), origin)
            ?: origin

    fun resetInstance() {
        party = null
        isTpSafe = true
        triggers.values.forEach {
            it.procced = false
            it.clearCurrentlyInsidePlayers()
        }
        activeAreas.forEach { it.fillWithMaterial(it.startingMaterial) }
        InstanceObjectiveManager.instanceObjectives[dungeon.id to id]?.abort()
        InstanceObjectiveManager.instanceIdForTrackedMobs.values.removeAll { it == id }
        InstanceObjectiveManager.dungeonIdForTrackedMobs.values.removeAll { it == id }
    }

    fun onInstanceStart() {
        triggers.values.forEach { it.applyMeta() }
        val pt = party ?: return
        pt.inGame = true
        pt.players.forEach {
            it.returnPosition = it.location.clone()
            it.returnGameMode = it.gameMode
            it.gameMode = GameMode.ADVENTURE
            val startingLocation = Location(
                    Bukkit.getWorld(ConfigManager.dungeonWorld),
                    startingPostion.x,
                    startingPostion.y,
                    startingPostion.z
            )
            it.teleport(
                    startingLocation,
                    PlayerTeleportEvent.TeleportCause.PLUGIN
            )
            it.sendFWDMessage("Good luck out there!")
        }
        pt.instance.isTpSafe = false
    }
    
    fun onInstanceFinish(givePoints: Boolean) {
        if (givePoints && dungeon.points != 0)
            party?.players
                    ?.map { it.uniqueId }
                    ?.let { DungeonCompletedEvent(it, dungeon.points.toFloat()) }
                    ?.let { Bukkit.getPluginManager().callEvent(it) }

        isTpSafe = true

        party?.players?.forEach {
            it.sendFWDMessage("${ChatColor.GREEN}Congratulations, you made it out alive!")
            it.returnPosition?.let { pos -> it.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN) }
            it.returnGameMode?.let { gm -> it.gameMode = gm }
        }

        party?.disband()
        resetInstance()
    }

    fun updateHlBlocks() {
        activeAreaHlFrameLocs.value = activeAreas.map { it.box.getFrontierBlocks() }.flatten().map { it.location }.toSet()
        triggerHlFrameLocs.value = triggers.values.map { it.box.getFrontierBlocks() }.flatten().map { it.location }.toSet()
    }

    fun toggleEditorHighlights() {
        if (doHighlightFrames.value) {
            doHighlightFrames.value = false
            return
        }
        doHighlightFrames.value = true
        updateHlBlocks()
        repeatedlySpawnParticles(
                Particle.DRIP_WATER,
                1,
                10,
                doHighlightFrames
        ) { activeAreaHlFrameLocs.value }
        repeatedlySpawnParticles(
                Particle.DRIP_LAVA,
                1,
                10,
                doHighlightFrames
        ) { triggerHlFrameLocs.value }
    }

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