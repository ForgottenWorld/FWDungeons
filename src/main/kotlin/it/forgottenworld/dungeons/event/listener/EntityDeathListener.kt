package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.model.combat.CombatObjective.Companion.combatObjective
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener: Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val co = event.entity.uniqueId.combatObjective ?: return
        co.onMobKilled(event.entity.uniqueId)
        if (!co.shouldBeRemoved) return
        if (co.aborting)
            co.aborting = false
        else {
            co.onAllKilled(co.instance)
            co.instance.instanceObjectives.remove(co)
        }

    }
}