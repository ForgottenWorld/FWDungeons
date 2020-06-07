package it.forgottenworld.dungeons.model.objective

import it.forgottenworld.dungeons.controller.MobTracker
import java.util.*

class InstanceObjective(
        private val instanceId: Int,
        private val mobsToKill: MutableList<UUID>,
        private val onAllKilled: () -> Unit) {

    fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        if (mobsToKill.isEmpty()) {
            MobTracker.instanceObjectives.remove(instanceId)
            onAllKilled()
        }
    }

}