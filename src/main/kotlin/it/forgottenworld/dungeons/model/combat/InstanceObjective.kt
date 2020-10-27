package it.forgottenworld.dungeons.model.combat

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.service.InstanceObjectiveService
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class InstanceObjective(
        private val instance: DungeonFinalInstance,
        private val mobsToKill: MutableList<UUID>,
        private val onAllKilled: (DungeonFinalInstance) -> Unit) {

    var active = true

    fun onMobKilled(uuid: UUID) {
        if (!active) return
        mobsToKill.remove(uuid)
        if (mobsToKill.isNotEmpty()) return
        InstanceObjectiveService.instanceObjectives.remove(instance.dungeon.id to instance.id)
        if (active) onAllKilled(instance)
    }

    fun abort() {
        active = false
        InstanceObjectiveService.instanceObjectives.remove(instance.dungeon.id to instance.id)
        mobsToKill
                .map { getEntity(it) }
                .filterIsInstance<LivingEntity>()
                .forEach { it.health = 0.0 }
    }
}