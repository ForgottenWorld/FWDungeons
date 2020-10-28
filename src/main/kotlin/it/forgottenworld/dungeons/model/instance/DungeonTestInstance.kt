package it.forgottenworld.dungeons.model.instance

import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.model.box.Box
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.async
import it.forgottenworld.dungeons.utils.launch
import it.forgottenworld.dungeons.utils.launchAsync
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

class DungeonTestInstance(
        override val id: Int,
        override val dungeon: EditableDungeon,
        override val origin: BlockVector,
        override val triggers: MutableMap<Int, Trigger>,
        override val activeAreas: MutableMap<Int, ActiveArea>,
        private val tester: Player) : DungeonInstance {

    init {
        startCheckingTriggers()
    }

    override val box = dungeon.box!!.withOrigin(origin)

    private var triggerHlFrameLocs = setOf<Location>()
    private var activeAreaHlFrameLocs = setOf<Location>()
    private var triggerParticleTask: BukkitTask? = null
    private var activeAreaParticleTask: BukkitTask? = null

    private var checkTriggers = true

    fun newInteractiveElement(type: InteractiveElementType, id: Int, box: Box) {
            when (type) {
                InteractiveElementType.TRIGGER -> launch {
                    checkTriggers = false
                    delay(1000)
                    triggers[id] = Trigger(id, box).apply { box.highlightAll() }
                    checkTriggers = true
                    startCheckingTriggers()
                }
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

        triggerParticleTask = repeatedlySpawnParticles(Particle.DRIP_LAVA, 1, 500) { triggerHlFrameLocs }
        activeAreaParticleTask = repeatedlySpawnParticles(Particle.DRIP_WATER, 1, 500) { activeAreaHlFrameLocs }
    }

    fun onDestroy() {
        if (triggerParticleTask != null) {
            triggerParticleTask?.cancel()
            activeAreaParticleTask?.cancel()
            triggerHlFrameLocs = setOf()
            activeAreaHlFrameLocs = setOf()
            return
        }
        checkTriggers = false
    }

    private fun checkTriggers(
            playerUuid: UUID,
            posVector: Vector,
            oldTriggerId: Int?
    ) = launchAsync {
        val triggerId = withContext(Dispatchers.async) {
            triggers.values.find {
                it.containsVector(posVector)
            }?.id
        }

        if (oldTriggerId == triggerId) return@launchAsync

        launch {
            Bukkit.getPluginManager().callEvent(TriggerEvent(
                    playerUuid,
                    triggerId ?: -1,
                    oldTriggerId != null
            ))
        }
    }

    private fun startCheckingTriggers() = launch {
        while (checkTriggers) {
            delay(500)
            checkTriggers(
                    tester.uniqueId,
                    tester.location.toVector(),
                    tester.collidingTrigger?.id
            )
        }
    }
}