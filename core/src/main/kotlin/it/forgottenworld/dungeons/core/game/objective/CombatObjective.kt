package it.forgottenworld.dungeons.core.game.objective

import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager.combatObjective
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class CombatObjective(
    val instance: DungeonInstanceImpl,
    private val mobsToKill: MutableList<UUID>,
    var onAllKilled: (DungeonInstanceImpl) -> Unit
) {

    private var aborting = false
    private val shouldBeRemoved
        get() = mobsToKill.isEmpty()

    fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        uuid.combatObjective = null
        if (!shouldBeRemoved) return
        if (aborting) {
            aborting = false
            return
        }
        onAllKilled(instance)
        instance.instanceObjectives.remove(this)
    }

    fun abort() {
        aborting = true
        mobsToKill
            .map { getEntity(it) }
            .filterIsInstance<LivingEntity>()
            .forEach { it.health = 0.0 }
    }
}