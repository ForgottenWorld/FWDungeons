package it.forgottenworld.dungeons.core.game.objective

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.objective.CombatObjective
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.*

class CombatObjectiveImpl @Inject constructor(
    @Assisted override val instance: DungeonInstance,
    @Assisted private val mobsToKill: MutableList<UUID>,
    @Assisted override var onAllKilled: (DungeonInstance) -> Unit,
    private val combatObjectiveManager: CombatObjectiveManager
) : CombatObjective {

    override var aborting = false

    override fun onMobKilled(uuid: UUID) {
        mobsToKill.remove(uuid)
        combatObjectiveManager.setEntityCombatObjective(uuid, null)
        if (mobsToKill.isNotEmpty() || aborting) return
        onAllKilled(instance)
        instance.instanceObjectives.remove(this)
    }

    override fun abort() {
        aborting = true
        mobsToKill
            .map(Bukkit::getEntity)
            .filterIsInstance<LivingEntity>()
            .forEach { it.health = 0.0 }
    }
}