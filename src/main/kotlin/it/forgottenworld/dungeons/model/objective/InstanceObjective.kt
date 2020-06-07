package it.forgottenworld.dungeons.model.objective

import java.util.*

class InstanceObjective(
        private val mobsToKill: MutableList<UUID>,
        private val onAllKilled: () -> Unit) {

    fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        if (mobsToKill.isEmpty())
            onAllKilled()
    }

}