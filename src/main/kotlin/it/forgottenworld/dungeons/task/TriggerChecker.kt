package it.forgottenworld.dungeons.task

import it.forgottenworld.dungeons.manager.DungeonManager
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.utils.bukkitThreadTimer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object TriggerChecker {

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
                ?.let { instance.triggers[it]?.onPlayerEnter(player) }
    }

    fun start() {
        task?.cancel()
        task = bukkitThreadTimer(20L, 10L) {
            for (instance in DungeonManager.dungeons.values
                    .flatMap { it.instances }
                    .filter { it.isTest || it.party?.inGame == true }) {
                instance.tester
                        ?.let { checkTrigger(instance, it) }
                        ?: (instance.party!!.players).forEach { checkTrigger(instance, it) }
            }
        }
    }
}

