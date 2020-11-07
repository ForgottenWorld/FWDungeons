package it.forgottenworld.dungeons.model.combat

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class CombatObjective(
        val instance: DungeonFinalInstance,
        private val mobsToKill: MutableList<UUID>,
        var onAllKilled: (DungeonFinalInstance) -> Unit) {

    var aborting = false
    val shouldBeRemoved
        get() = mobsToKill.isEmpty()


    fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        uuid.combatObjective = null
    }

    fun abort() {
        aborting = true
        mobsToKill
                .map { getEntity(it) }
                .filterIsInstance<LivingEntity>()
                .forEach { it.health = 0.0 }
    }

    companion object {

        private val entityCombatObjectives = mutableMapOf<UUID, CombatObjective>()

        var UUID.combatObjective
            get() = entityCombatObjectives[this]
            set(value) {
                value?.let {
                    entityCombatObjectives[this] = it
                } ?: entityCombatObjectives.remove(this)
            }
    }
}