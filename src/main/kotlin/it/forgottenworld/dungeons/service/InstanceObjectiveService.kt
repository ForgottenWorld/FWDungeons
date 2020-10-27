package it.forgottenworld.dungeons.service

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper
import it.forgottenworld.dungeons.model.combat.InstanceObjective
import it.forgottenworld.dungeons.model.combat.MobSpawnData
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.*

object InstanceObjectiveService {

    val dungeonIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceIdForTrackedMobs = mutableMapOf<UUID, Int>()
    val instanceObjectives = mutableMapOf<Pair<Int, Int>, InstanceObjective>()

    fun onEntityDeath(uuid: UUID) {
        val id = instanceIdForTrackedMobs[uuid] ?: return
        val dId = dungeonIdForTrackedMobs[uuid]!!
        instanceObjectives[dId to id]?.onMobKilled(uuid)
        instanceIdForTrackedMobs.remove(uuid)
        dungeonIdForTrackedMobs.remove(uuid)
    }
    
    fun attachNewObjectiveToInstance(
            instance: DungeonFinalInstance,
            mobs: List<MobSpawnData>,
            onAllKilled: (DungeonFinalInstance) -> Unit) {

        val msd = mobs.mapNotNull {
            spawnMob(it.isMythic,
                    it.mob,
                    (instance.activeAreas[it.activeAreaId] ?: error("")).getRandomLocationOnFloor()
                            .clone()
                            .add(0.5, 0.5, 0.5)
            )?.also { uuid ->
                dungeonIdForTrackedMobs[uuid] = instance.dungeon.id
                instanceIdForTrackedMobs[uuid] = instance.id
            }
        }.toMutableList()

        instanceObjectives[instance.dungeon.id to instance.id] = InstanceObjective(instance, msd, onAllKilled)
    }

    private fun spawnMob(isMythic: Boolean, type: String, location: Location) =
            if (isMythic) spawnMythicMob(type, location)
            else spawnVanillaMob(type, location)

    private fun spawnMythicMob(type: String, location: Location) =
            BukkitAPIHelper().spawnMythicMob(type, location).uniqueId

    private fun spawnVanillaMob(type: String, location: Location) =
            location.world?.spawnEntity(location, EntityType.valueOf(type))?.uniqueId
}