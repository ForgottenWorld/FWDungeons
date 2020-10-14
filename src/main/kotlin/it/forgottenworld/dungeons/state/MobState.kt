package it.forgottenworld.dungeons.state

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.model.activearea.ActiveArea
import it.forgottenworld.dungeons.model.objective.InstanceObjective
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.*

enum class MobType { MYTHIC, VANILLA }
data class MobSpawnData(
        val activeArea: ActiveArea,
        val mob: String,
        val type: MobType)

object MobState {

    val dungeonIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceObjectives = mutableMapOf<Pair<Int, Int>, InstanceObjective>()

    fun attachNewObjectiveToInstance(
            dungeonId: Int,
            instanceId: Int,
            mobs: List<MobSpawnData>,
            onAllKilled: () -> Unit) {
        instanceObjectives[dungeonId to instanceId] = InstanceObjective(
                dungeonId,
                instanceId,
                mobs.mapNotNull {
                    (if (it.type == MobType.VANILLA) ::spawnMob else ::spawnMythicMob)(
                            it.mob,
                            it.activeArea
                                    .getRandomLocationOnFloor()
                                    .clone()
                                    .add(0.5, 0.5, 0.5)
                    )?.also { uuid ->
                        dungeonIdForTrackedMobs[uuid] = dungeonId
                        instanceIdForTrackedMobs[uuid] = instanceId
                    }
                }.toMutableList(),
                onAllKilled
        )
    }

    private fun spawnMythicMob(type: String, location: Location) =
        BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnMob(type: String, location: Location) =
        location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId
}