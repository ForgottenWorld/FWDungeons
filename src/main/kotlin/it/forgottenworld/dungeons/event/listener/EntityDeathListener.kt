package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.service.InstanceObjectiveService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener: Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        InstanceObjectiveService.onEntityDeath(event.entity.uniqueId)
    }
}