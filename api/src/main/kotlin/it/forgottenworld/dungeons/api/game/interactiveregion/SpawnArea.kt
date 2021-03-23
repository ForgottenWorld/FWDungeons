package it.forgottenworld.dungeons.api.game.interactiveregion

import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Location

interface SpawnArea : InteractiveRegion, Storage.Storable {

    val heightMap: Array<IntArray>

    fun getRandomLocationOnFloor(dungeonInstance: DungeonInstance): Location
}