package it.forgottenworld.dungeons.core.game.objective

import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import java.util.*

object CombatObjectiveManager {

    private val entityCombatObjectives = mutableMapOf<UUID, CombatObjective>()

    var UUID.combatObjective
        get() = entityCombatObjectives[this]
        set(value) {
            value?.let {
                entityCombatObjectives[this] = it
            } ?: entityCombatObjectives.remove(this)
        }
}