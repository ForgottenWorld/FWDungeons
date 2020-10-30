package it.forgottenworld.dungeons.model.instance

import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.ActiveArea
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import it.forgottenworld.dungeons.utils.ktx.getPlayer
import it.forgottenworld.dungeons.utils.launch
import it.forgottenworld.dungeons.utils.launchAsync
import it.forgottenworld.dungeons.utils.mapObserver
import it.forgottenworld.dungeons.utils.repeatedlySpawnParticles
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.util.*

class DungeonTestInstance(
        override val id: Int,
        override val dungeon: EditableDungeon,
        override val origin: BlockVector,
        private val tester: UUID) : DungeonInstance {

    override val box = dungeon.box!!.withOrigin(origin)

    override val triggers: Map<Int, Trigger> by mapObserver(
        dungeon.triggers,
        {
            val map = it.map { (k, v) -> k to v.withContainerOrigin(BlockVector(0, 0, 0), origin) }.toMap()
            launch {
                checkTriggers = false
                updateHlBlocks()
                delay(1000)
            }
            it
        },
        {
            checkTriggers = true
            startCheckingTriggers()
        }
    )

    override val activeAreas: Map<Int, ActiveArea> by mapObserver(dungeon.activeAreas) {
        it.map { (k, v) ->
            k to v.withContainerOrigin(BlockVector(0, 0, 0), origin) }
                .toMap()
                .also { nm -> nm.values.lastOrNull()?.box?.highlightAll() }
        updateHlBlocks()
    }

    private var triggerHlFrameLocs = setOf<Location>()
    private var activeAreaHlFrameLocs = setOf<Location>()
    private var triggerParticleTask: BukkitTask? = null
    private var activeAreaParticleTask: BukkitTask? = null
    private var checkTriggers = true

    init {
        startCheckingTriggers()
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

    private fun startCheckingTriggers() = launch {
        val player = getPlayer(tester) ?: return@launch
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