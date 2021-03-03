package it.forgottenworld.dungeons.core.game.objective

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import java.util.*

@Singleton
class CombatObjectiveManager {

    private val entityCombatObjectives = mutableMapOf<UUID, CombatObjective>()

    fun getEntityCombatObjective(uuid: UUID) = entityCombatObjectives[uuid]

    fun setEntityCombatObjective(uuid: UUID, combatObjective: CombatObjective?) {
        if (combatObjective != null) {
            entityCombatObjectives[uuid] = combatObjective
        } else {
            entityCombatObjectives.remove(uuid)
        }
    }
}