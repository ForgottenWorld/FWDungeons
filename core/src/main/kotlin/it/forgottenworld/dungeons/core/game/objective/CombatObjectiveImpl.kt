package it.forgottenworld.dungeons.core.game.objective

import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager.combatObjective
import org.bukkit.Bukkit.getEntity
import org.bukkit.entity.LivingEntity
import java.util.*

class CombatObjectiveImpl(
    override val instance: DungeonInstance,
    private val mobsToKill: MutableList<UUID>,
    override var onAllKilled: (DungeonInstance) -> Unit
) : CombatObjective {

    override var aborting = false

    override fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        uuid.combatObjective = null
        if (mobsToKill.isNotEmpty()) return
        if (aborting) {
            aborting = false
            return
        }
        onAllKilled(instance)
        instance.instanceObjectives.remove(this)
    }

    override fun abort() {
        aborting = true
        mobsToKill
            .map { getEntity(it) }
            .filterIsInstance<LivingEntity>()
            .forEach { it.health = 0.0 }
    }
}