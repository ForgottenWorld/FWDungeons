package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.state.MobState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener: Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent?) {

        val entity = event?.entity ?: return

        MobState.instanceIdForTrackedMobs[entity.uniqueId]?.let {
            val dId = MobState.dungeonIdForTrackedMobs[entity.uniqueId]!!
            MobState.instanceObjectives[MobState.DungeonAndInstanceIdPair(dId, it)]?.onMobKilled(entity.uniqueId)
            MobState.instanceIdForTrackedMobs.remove(entity.uniqueId)
            MobState.dungeonIdForTrackedMobs.remove(entity.uniqueId)
        }
    }
}