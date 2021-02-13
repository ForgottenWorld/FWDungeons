package it.forgottenworld.dungeons.game.instance

import it.forgottenworld.dungeons.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.game.interactiveregion.ActiveArea
import it.forgottenworld.dungeons.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.game.interactiveregion.Trigger.ActivationHandler.Companion.collidingTrigger
import it.forgottenworld.dungeons.utils.ParticleSpammer
import it.forgottenworld.dungeons.utils.getPlayer
import it.forgottenworld.dungeons.utils.launch
import it.forgottenworld.dungeons.utils.launchAsync
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

class DungeonTestInstance(
    override val dungeon: EditableDungeon,
    override val origin: BlockVector,
    private val tester: UUID) : DungeonInstance {

    override val id = -1
    override val box = dungeon.box!!.withOrigin(origin)

    private var triggerDetectionJob: Job? = null

    override var triggers = dungeon.triggers
    override var activeAreas = dungeon.activeAreas

    private var hlFrames = false

    fun updateTriggers(newTriggers: Map<Int, Trigger>) {
        triggers = newTriggers.mapValues { (_, t) ->
            t.withContainerOrigin(
                BlockVector(0, 0, 0),
                origin
            )
        }
        updateTriggerParticleSpammers()
    }

    fun updateActiveAreas(newActiveAreas: Map<Int, ActiveArea>) {
        activeAreas = newActiveAreas.mapValues { (_, t) ->
            t.withContainerOrigin(
                BlockVector(0, 0, 0),
                origin
            )
        }
        updateActiveAreaParticleSpammers()
    }

    fun highlightNewInteractiveRegion(interactiveRegion: InteractiveRegion) {
        interactiveRegion.withContainerOrigin(BlockVector(0, 0, 0), origin).also { it.box.highlightAll() }
    }

    private var triggerParticleSpammer: ParticleSpammer? = null
    private var activeAreaParticleSpammer: ParticleSpammer? = null

    init {
        startCheckingTriggers()
    }

    private fun updateTriggerParticleSpammers() {
        if (!hlFrames) return
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = ParticleSpammer(Particle.DRIP_LAVA, 1, 500, triggers.values.flatMap { it.box.getFrontier() })
    }

    private fun updateActiveAreaParticleSpammers() {
        if (!hlFrames) return
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = ParticleSpammer(Particle.DRIP_WATER, 1, 500, activeAreas.values.flatMap { it.box.getFrontier() })
    }

    private fun updateParticleSpammers() {
        if (!hlFrames) return
        updateTriggerParticleSpammers()
        updateActiveAreaParticleSpammers()
    }

    private fun stopParticleSpammers() {
        triggerParticleSpammer?.stop()
        triggerParticleSpammer = null
        activeAreaParticleSpammer?.stop()
        activeAreaParticleSpammer = null
    }

    fun toggleEditorHighlights() {
        hlFrames = !hlFrames
        if (hlFrames)
            updateParticleSpammers()
        else
            stopParticleSpammers()
    }

    fun onDestroy() {
        stopParticleSpammers()
        triggerDetectionJob?.cancel()
        triggerDetectionJob = null
    }

    private fun checkTriggers(
        playerUuid: UUID,
        posVector: Vector,
        oldTriggerId: Int?
    ) = launchAsync {
        val triggerId = triggers.values.find { it.containsVector(posVector) }?.id

        if (oldTriggerId == triggerId) return@launchAsync

        launch {
            Bukkit.getPluginManager().callEvent(Trigger.Event(
                playerUuid,
                triggerId ?: -1,
                oldTriggerId != null
            ))
        }
    }

    fun stopCheckingTriggers() {
        triggerDetectionJob?.cancel()
        triggerDetectionJob = null
    }

    fun startCheckingTriggers() {
        triggerDetectionJob = launch {
            val player = getPlayer(tester) ?: return@launch
            while (true) {
                delay(500)
                checkTriggers(
                    tester,
                    player.location.toVector(),
                    tester.collidingTrigger?.id
                )
            }
        }
    }
}