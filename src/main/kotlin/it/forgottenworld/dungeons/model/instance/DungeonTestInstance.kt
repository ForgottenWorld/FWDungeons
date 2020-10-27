package it.forgottenworld.dungeons.model.instance

import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.task.TriggerChecker
import it.forgottenworld.dungeons.utils.TypeWrapper
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.BlockVector

class DungeonTestInstance(
        override val id: Int,
        override val dungeon: EditableDungeon,
        override val origin: BlockVector,
        override val triggers: MutableMap<Int, Trigger>,
        override val activeAreas: MutableMap<Int, ActiveArea>,
        val tester: Player) : DungeonInstance {

    init {
        TriggerChecker.activeInstances.add(this)
    }

    override val box = dungeon.box!!.withOrigin(origin)

    var doHighlightFrames = TypeWrapper(false)
    private var triggerHlFrameLocs = setOf<Location>()
    private var activeAreaHlFrameLocs = setOf<Location>()
    private var triggerParticleTask: BukkitTask? = null
    private var activeAreaParticleTask: BukkitTask? = null

    fun newInteractiveElement(type: InteractiveElementType, id: Int, box: Box) {
        when (type) {
            InteractiveElementType.TRIGGER -> triggers[id] = Trigger(id, box).apply { box.highlightAll() }
            InteractiveElementType.ACTIVE_AREA -> activeAreas[id] = ActiveArea(id, box).apply { box.highlightAll() }
        }
        updateHlBlocks()
    }

    private fun updateHlBlocks() {
        activeAreaHlFrameLocs = activeAreas
                .values
                .flatMap { aa -> aa.box.getFrontierBlocks().map { it.location } }.toSet()
        triggerHlFrameLocs = triggers
                .values
                .flatMap { t -> t.box.getFrontierBlocks().map { it.location } }.toSet()
    }

    fun toggleEditorHighlights() {

        if (triggerParticleTask != null) {
            triggerParticleTask?.cancel()
            activeAreaParticleTask?.cancel()
            triggerHlFrameLocs = setOf()
            activeAreaHlFrameLocs = setOf()
            return
        }

        updateHlBlocks()

        triggerParticleTask = repeatedlySpawnParticles(Particle.DRIP_LAVA, 1, 10) { triggerHlFrameLocs }
        activeAreaParticleTask = repeatedlySpawnParticles(Particle.DRIP_WATER, 1, 10) { activeAreaHlFrameLocs }
    }
}