package it.forgottenworld.dungeons.model

import it.forgottenworld.dungeons.manager.InstanceObjectiveManager
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class InstanceObjective(
        private val instance: DungeonInstance,
        private val mobsToKill: MutableList<UUID>,
        private val onAllKilled: (DungeonInstance) -> Unit) {

    var active = true

    fun onMobKilled(uuid: UUID) {
        if (!active) return
        mobsToKill.remove(uuid)
        if (mobsToKill.isNotEmpty()) return
        InstanceObjectiveManager.instanceObjectives.remove(instance.dungeon.id to instance.id)
        if (active) onAllKilled(instance)
    }

    fun abort() {
        active = false
        InstanceObjectiveManager.instanceObjectives.remove(instance.dungeon.id to instance.id)
        mobsToKill
                .map { getEntity(it) }
                .filterIsInstance<LivingEntity>()
                .forEach { it.health = 0.0 }
    }
}