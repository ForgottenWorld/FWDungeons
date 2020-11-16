package it.forgottenworld.dungeons.model.instance

import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.event.listener.TriggerActivationHandler.Companion.collidingTrigger
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.ParticleSpammer
import it.forgottenworld.dungeons.utils.ktx.getPlayer
import it.forgottenworld.dungeons.utils.ktx.launch
import it.forgottenworld.dungeons.utils.ktx.launchAsync
import it.forgottenworld.dungeons.utils.mapObserver
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

    override val triggers: Map<Int, Trigger> by mapObserver(
            dungeon.triggers,
            { (k,v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), origin).also { it.box.highlightAll() } }
    ) { updateHlBlocks() }

    override val activeAreas: Map<Int, ActiveArea> by mapObserver(
            dungeon.activeAreas,
            { (k,v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), origin).also { it.box.highlightAll() } }
    ) { updateHlBlocks() }

    private var triggerHlFrames = setOf<BlockVector>()
    private fun getTriggerHlFrames() = triggerHlFrames
    private var activeAreaHlFrames = setOf<BlockVector>()
    private fun getActiveAreaHlFrames() = activeAreaHlFrames
    private var triggerParticleTask: ParticleSpammer? = null
    private var activeAreaParticleTask: ParticleSpammer? = null
    private var checkTriggers = true

    init {
        startCheckingTriggers()
    }

    private fun updateHlBlocks() {
        activeAreaHlFrames = activeAreas
                .values
                .flatMap { aa -> aa.box.getFrontier() }.toSet()
        triggerHlFrames = triggers
                .values
                .flatMap { t -> t.box.getFrontier() }.toSet()
    }

    private fun stopSpammers(): Boolean {
        if (triggerParticleTask == null) return false
        triggerParticleTask?.stop()
        triggerParticleTask = null
        activeAreaParticleTask?.stop()
        activeAreaParticleTask = null
        triggerHlFrames = setOf()
        activeAreaHlFrames = setOf()
        return true
    }

    suspend fun stopCheckingTriggersAndWait() {
        checkTriggers = false
        delay(1000)
    }

    fun toggleEditorHighlights() {
        if (stopSpammers()) return
        updateHlBlocks()
        triggerParticleTask = ParticleSpammer(Particle.DRIP_LAVA, 1, 500, ::getTriggerHlFrames)
        activeAreaParticleTask = ParticleSpammer(Particle.DRIP_WATER, 1, 500, ::getActiveAreaHlFrames)
    }

    fun onDestroy() {
        stopSpammers()
        checkTriggers = false
    }

    private fun checkTriggers(
            playerUuid: UUID,
            posVector: Vector,
            oldTriggerId: Int?
    ) = launchAsync {
        val triggerId = triggers.values.find { it.containsVector(posVector) }?.id

        if (oldTriggerId == triggerId) return@launchAsync

        launch {
            Bukkit.getPluginManager().callEvent(TriggerEvent(
                    playerUuid,
                    triggerId ?: -1,
                    oldTriggerId != null
            ))
        }
    }

    fun startCheckingTriggers() = launch {
        val player = getPlayer(tester) ?: return@launch
        checkTriggers = true
        while (checkTriggers) {
            delay(500)
            checkTriggers(
                    tester,
                    player.location.toVector(),
                    tester.collidingTrigger?.id
            )
        }
    }
}