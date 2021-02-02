package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.model.combat.CombatObjective.Companion.combatObjective
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        event.entity.uniqueId.combatObjective?.onMobKilled(event.entity.uniqueId)
    }
}