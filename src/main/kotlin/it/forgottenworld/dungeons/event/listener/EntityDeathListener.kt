package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.controller.MobTracker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener: Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent?) {

        val entity = event?.entity ?: return

        MobTracker.instanceIdForTrackedMobs[entity.uniqueId]?.let {
            MobTracker.instanceObjectives[it]?.onMobKilled(entity.uniqueId)
            MobTracker.instanceIdForTrackedMobs.remove(entity.uniqueId)
        }
    }
}