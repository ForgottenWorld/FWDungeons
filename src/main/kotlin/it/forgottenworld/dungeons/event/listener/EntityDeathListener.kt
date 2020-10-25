package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.manager.InstanceObjectiveManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener: Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent?) {

        val entity = event?.entity ?: return

        InstanceObjectiveManager.run {
            instanceIdForTrackedMobs[entity.uniqueId]?.let {
                val dId = dungeonIdForTrackedMobs[entity.uniqueId]!!
                instanceObjectives[dId to it]?.onMobKilled(entity.uniqueId)
                instanceIdForTrackedMobs.remove(entity.uniqueId)
                dungeonIdForTrackedMobs.remove(entity.uniqueId)
            }
        }
    }
}