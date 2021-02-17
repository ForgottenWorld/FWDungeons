package it.forgottenworld.dungeons.game.instance

import it.forgottenworld.dungeons.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.utils.ParticleSpammer
import it.forgottenworld.dungeons.utils.Vector3i
import kotlinx.coroutines.Job
import org.bukkit.Particle

class DungeonTestInstance(
    override val dungeon: EditableDungeon,
    override val origin: Vector3i
) : DungeonInstance {

    override val id = -1
    override val box = dungeon.box!!.withOrigin(origin)

    private var triggerDetectionJob: Job? = null

    private var hlFrames = false

    fun updateTriggers(newTriggers: Collection<Trigger>) {
        updateTriggerParticleSpammers(newTriggers)
    }

    fun updateActiveAreas(newActiveAreas: Collection<ActiveArea>) {
        updateActiveAreaParticleSpammers(newActiveAreas)
    }

    fun highlightNewInteractiveRegion(interactiveRegion: InteractiveRegion) {
        interactiveRegion.withContainerOrigin(Vector3i(0, 0, 0), origin).also { it.box.highlightAll() }
    }

    private var triggerParticleSpammer: ParticleSpammer? = null
    private var activeAreaParticleSpammer: ParticleSpammer? = null

    private fun updateTriggerParticleSpammers(newTriggers: Collection<Trigger>) {
        if (!hlFrames) return
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = ParticleSpammer(
            Particle.DRIP_LAVA,
            1,
            500,
            newTriggers.flatMap {
                it.box.withContainerOrigin(Vector3i.ZERO, origin).getFrame()
            }
        )
    }

    private fun updateActiveAreaParticleSpammers(newActiveAreas: Collection<ActiveArea>) {
        if (!hlFrames) return
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = ParticleSpammer(
            Particle.DRIP_WATER,
            1,
            500,
            newActiveAreas.flatMap {
                it.box.withContainerOrigin(Vector3i.ZERO, origin).getFrame()
            }
        )
    }

    private fun updateParticleSpammers() {
        if (!hlFrames) return
        updateTriggerParticleSpammers(dungeon.triggers.values)
        updateActiveAreaParticleSpammers(dungeon.activeAreas.values)
    }

    private fun stopParticleSpammers() {
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = null
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = null
    }

    fun toggleEditorHighlights() {
        hlFrames = !hlFrames
        if (hlFrames) {
            updateParticleSpammers()
        } else {
            stopParticleSpammers()
        }
    }

    fun onDestroy() {
        stopParticleSpammers()
        triggerDetectionJob?.cancel()
        triggerDetectionJob = null
    }
}