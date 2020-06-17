package it.forgottenworld.dungeons.state

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.objective.InstanceObjective
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.*

enum class MobType { MYTHIC, VANILLA }
data class MobSpawnData(val activeArea: ActiveArea, val mob: String, val type: MobType)

object MobTracker {
    val instanceIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceObjectives = mutableMapOf<Int, InstanceObjective>()

    fun attachNewObjectiveToInstance(
            instanceId: Int,
            mobs: Set<MobSpawnData>,
            onAllKilled: () -> Unit) {
        if (instanceObjectives.contains(instanceId)) return
        instanceObjectives[instanceId] = InstanceObjective(
                instanceId,
                mobs.mapNotNull {
                    if (it.type == MobType.VANILLA) {
                        spawnMob(it.mob,
                                it.activeArea
                                        .getRandomLocationOnFloor()
                                        .clone()
                                        .add(0.5, 0.5, 0.5)
                                )?.also { uuid ->
                                            instanceIdForTrackedMobs[uuid] = instanceId
                                        }
                    } else {
                        spawnMythicMob(it.mob,
                                it.activeArea
                                        .getRandomLocationOnFloor()
                                        .clone()
                                        .add(0.5, 0.5, 0.5)
                                )?.also { uuid ->
                                            instanceIdForTrackedMobs[uuid] = instanceId
                                        }
                    } }.toMutableList(),
                onAllKilled
        )
        return
    }

    private fun spawnMythicMob(type: String, location: Location): UUID? =
        BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnMob(type: String, location: Location): UUID? =
        location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId
}