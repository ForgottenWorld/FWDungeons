package it.forgottenworld.dungeons.core.game.objective

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import java.util.*

interface CombatObjectiveFactory {
    fun create(
        instance: DungeonInstance,
        mobsToKill: MutableList<UUID>,
        onAllKilled: (DungeonInstance) -> Unit
    ) : CombatObjective
}