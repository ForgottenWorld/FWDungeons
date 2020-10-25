package it.forgottenworld.dungeons.manager

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.model.InstanceObjective
import it.forgottenworld.dungeons.model.MobSpawnData
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.*

object InstanceObjectiveManager {

    val dungeonIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceObjectives = mutableMapOf<Pair<Int, Int>, InstanceObjective>()

    fun attachNewObjectiveToInstance(
            instance: DungeonInstance,
            mobs: List<MobSpawnData>,
            onAllKilled: (DungeonInstance) -> Unit) {
        instanceObjectives[instance.dungeon.id to instance.id] = InstanceObjective(
                instance,
                mobs.mapNotNull {
                    spawnMob(it.isMythic,
                            it.mob,
                            instance.getActiveAreaById(it.activeAreaId)
                                    !!.getRandomLocationOnFloor()
                                    .clone()
                                    .add(0.5, 0.5, 0.5)
                    )?.also { uuid ->
                        dungeonIdForTrackedMobs[uuid] = instance.dungeon.id
                        instanceIdForTrackedMobs[uuid] = instance.id
                    }
                }.toMutableList(),
                onAllKilled
        )
    }

    private fun spawnMob(isMythic: Boolean, type: String, location: Location): UUID? =
            if (isMythic)
                spawnMythicMob(type, location)
            else
                spawnVanillaMob(type, location)

    private fun spawnMythicMob(type: String, location: Location) =
        BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnVanillaMob(type: String, location: Location) =
        location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId
}