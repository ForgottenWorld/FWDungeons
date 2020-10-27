package it.forgottenworld.dungeons.task

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.model.instance.DungeonInstance
import it.forgottenworld.dungeons.model.instance.DungeonTestInstance
import it.forgottenworld.dungeons.service.DungeonService.collidingTrigger
import it.forgottenworld.dungeons.utils.bukkitThreadTimer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object TriggerChecker {

    val activeInstances = mutableSetOf<DungeonInstance>()

    private var task: BukkitTask? = null

    private fun checkTrigger(instance: DungeonInstance, player: Player) {
        val collidingTrigger = player.collidingTrigger
        if (collidingTrigger != null) {
            if (!collidingTrigger.isPlayerInside(player))
                collidingTrigger.onPlayerExit(player)
            return
        }

        player.location.block
                .getMetadata("FWD_triggers")
                .firstOrNull()
                ?.asInt()
                ?.let { instance.triggers[it]?.onPlayerEnter(player, instance) }
    }

    fun start() {
        task?.cancel()
        task = bukkitThreadTimer(20L, 10L) {
            for (instance in activeInstances) {
                when(instance) {
                    is DungeonTestInstance -> checkTrigger(instance, instance.tester)
                    is DungeonFinalInstance -> (instance.players).forEach { checkTrigger(instance, it) }
                }
            }
        }
    }
}

