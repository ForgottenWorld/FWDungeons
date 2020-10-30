package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.manager.InstanceObjectiveManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener: Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val uuid = event.entity.uniqueId
        InstanceObjectiveManager.entityObjectives[uuid]?.onMobKilled(uuid)
    }
}