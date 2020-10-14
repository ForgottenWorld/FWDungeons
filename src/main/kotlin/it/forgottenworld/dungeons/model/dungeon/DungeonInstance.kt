package it.forgottenworld.dungeons.model.dungeon

import it.forgottenworld.dungeons.event.DungeonCompletedEvent
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.party.Party
import it.forgottenworld.dungeons.model.trigger.Trigger
import it.forgottenworld.dungeons.state.DungeonState
import it.forgottenworld.dungeons.state.DungeonState.returnGameMode
import it.forgottenworld.dungeons.state.DungeonState.returnPosition
import it.forgottenworld.dungeons.state.MobState
import it.forgottenworld.dungeons.utils.TypeWrapper
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.withRefSystemOrigin
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.BlockVector

class DungeonInstance(
        val id: Int,
        val dungeon: Dungeon,
        private val origin: BlockVector,
        val triggers: MutableMap<Int, Trigger>,
        val activeAreas: MutableList<ActiveArea>) {

    private val activeAreasIdMap = activeAreas.map { it.id to it }.toMap()
    var doHighlightFrames = TypeWrapper(false)
    var activeAreaHlFrameLocs = TypeWrapper(setOf<Location>())
    var triggerHlFrameLocs = TypeWrapper(setOf<Location>())

    companion object {
        fun fromConfig(config: ConfigurationSection): DungeonInstance? {
            val dungeon = DungeonState.dungeons[config.getInt("dungeon_id")] ?: return null
            val instOrigin = BlockVector(
                    config.getInt("x"),
                    config.getInt("y"),
                    config.getInt("z"))
            return DungeonInstance(
                    config.getInt("instance_id"),
                    dungeon,
                    instOrigin,
                    dungeon.triggers.map {
                        Trigger(it.id,
                                dungeon,
                                it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                                it.effectParser,
                                it.requiresWholeParty
                        ).apply { label = it.label }
                    }.map { it.id to it }.toMap().toMutableMap(),
                    dungeon.activeAreas.map {
                        ActiveArea(it.id,
                                it.box.withContainerOrigin(BlockVector(0,0,0), instOrigin),
                                it.startingMaterial
                        ).apply { label = it.label}
                    }.toMutableList()
            ).apply {
                triggers.values.forEach { it.parseEffect(this) }
                resetInstance()
            }
        }
    }

    init {
        triggers.values.forEach { it.applyMeta(this) }
    }

    fun getActiveAreaById(id: Int) = activeAreasIdMap[id]

    var party: Party? = null

    val box
        get() = dungeon.box.withOrigin(origin)

    val startingPostion
        get() = dungeon.startingLocation.withRefSystemOrigin(BlockVector(0,0,0), origin)

    fun resetInstance() {
        party = null
        triggers.values.forEach {
            it.procced = false
            it.clearCurrentlyInsidePlayers()
        }
        activeAreas.forEach { it.fillWithMaterial(it.startingMaterial) }
        MobState.instanceObjectives[dungeon.id to id]?.abort()
        MobState.instanceIdForTrackedMobs.values.removeAll { it == id }
        MobState.dungeonIdForTrackedMobs.values.removeAll { it == id }
    }

    fun onInstanceFinish(givePoints: Boolean) {
        if (givePoints && dungeon.points != 0)
            party?.players
                    ?.map { it.uniqueId }
                    ?.let { DungeonCompletedEvent(it, dungeon.points.toFloat()) }
                    ?.let { Bukkit.getPluginManager().callEvent(it) }

        party?.players?.forEach {
            it.sendFWDMessage("${ChatColor.GREEN}Congratulations, you made it out alive!")
            it.returnPosition?.let { pos -> it.teleport(pos) }
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
        if (doHighlightFrames.value)
            doHighlightFrames.value = false
        else {
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
    }

    fun toConfig(config: ConfigurationSection) {
        config.run {
            set("dungeon_id", dungeon.id)
            set("setinstance_id", id)
            set("x", origin.x)
            set("y", origin.y)
            set("z", origin.z)
        }
    }
}