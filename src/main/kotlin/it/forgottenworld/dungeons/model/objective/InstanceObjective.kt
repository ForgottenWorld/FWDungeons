package it.forgottenworld.dungeons.model.objective

import it.forgottenworld.dungeons.state.MobState
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class InstanceObjective(
        private val dungeonId: Int,
        private val instanceId: Int,
        private val mobsToKill: MutableList<UUID>,
        private val onAllKilled: () -> Unit) {
    var active = true

    fun onMobKilled(uuid: UUID) {
        if (!active) return
        mobsToKill.remove(uuid)
        if (mobsToKill.isEmpty()) {
            MobState.instanceObjectives.remove(MobState.DungeonAndInstanceIdPair(dungeonId, instanceId))
            if (active) onAllKilled()
        }
    }

    fun abort() {
        active = false
        MobState.instanceObjectives.remove(MobState.DungeonAndInstanceIdPair(dungeonId, instanceId))
        mobsToKill.forEach {
            val entity = getEntity(it)
            if (entity != null && entity is LivingEntity) {
                entity.health = 0.0
            }
        }
    }

}