package it.forgottenworld.dungeons.controller

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.objective.InstanceObjective
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.*

object MobTracker {
    val trackedMobsForInstanceId = mutableMapOf<UUID, Int>()
    val instanceObjectives = mutableMapOf<Int, InstanceObjective>()

    fun attachNewObjectiveToInstance(
            instanceId: Int,
            mobs: List<String>,
            mythicMobs: List<String>,
            activeArea: ActiveArea,
            onAllKilled: () -> Unit) {
        if (instanceObjectives.contains(instanceId)) return
        InstanceObjective(
                (mobs.mapNotNull { spawnMob(it, activeArea.getRandomLocationOnFloor())?.also { uuid ->
                    trackedMobsForInstanceId[uuid] = instanceId
                } } +
                    mythicMobs.mapNotNull { spawnMythicMob(it, activeArea.getRandomLocationOnFloor())?.also {uuid ->
                        trackedMobsForInstanceId[uuid] = instanceId
                    } }).toMutableList(),
                onAllKilled
        )
        return
    }

    private fun spawnMythicMob(type: String, location: Location): UUID? =
        BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnMob(type: String, location: Location): UUID? =
        location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId
}