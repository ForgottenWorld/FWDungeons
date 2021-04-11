package it.forgottenworld.dungeons.core.game.dungeon.subelement.interactiveregion.spawnarea

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.api.math.Box
import it.forgottenworld.dungeons.api.math.Vector3d
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.storage.Configuration
import org.bukkit.Location
import javax.annotation.Nullable

class SpawnAreaImpl @Inject constructor(
    @Assisted override val id: Int,
    @Assisted override val box: Box,
    @Assisted override val heightMap: Array<IntArray>,
    @Nullable @Assisted override var label: String? = null,
    private val configuration : Configuration
) : SpawnArea, Storage.Storable {

    private val possibleSpawnLocations = heightMap.flatMapIndexed { x, yz ->
        yz.mapIndexed { z, y ->
            if (y == -1) null else Vector3d(x + 0.5, y.toDouble(), z + 0.5)
        }.filterNotNull()
    }

    override fun getRandomLocationOnFloor(dungeonInstance: DungeonInstance): Location {
        val spawnLocVector = possibleSpawnLocations.random()
        return Location(
            configuration.dungeonWorld,
            dungeonInstance.origin.x + box.origin.x + spawnLocVector.x,
            dungeonInstance.origin.y + box.origin.y + spawnLocVector.y,
            dungeonInstance.origin.z + box.origin.z + spawnLocVector.z
        )
    }
}