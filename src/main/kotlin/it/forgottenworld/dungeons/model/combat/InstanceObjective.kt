package it.forgottenworld.dungeons.model.combat

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class InstanceObjective(
        private val instance: DungeonFinalInstance,
        private val mobsToKill: MutableList<UUID>,
        private val onAllKilled: (DungeonFinalInstance) -> Unit) {

    fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        uuid.instanceObjective = null
        if (mobsToKill.isNotEmpty()) return
        instance.instanceObjectives.remove(this)
        onAllKilled(instance)
    }

    fun abort() {
        instance.instanceObjectives.remove(this)
        mobsToKill
                .map { getEntity(it) }
                .filterIsInstance<LivingEntity>()
                .forEach { it.health = 0.0 }
    }

    companion object {

        private val entityObjectives = mutableMapOf<UUID, InstanceObjective>()

        var UUID.instanceObjective
            get() = entityObjectives[this]
            set(value) {
                value?.let {
                    entityObjectives[this] = it
                } ?: entityObjectives.remove(this)
            }
    }
}