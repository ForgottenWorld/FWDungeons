package it.forgottenworld.dungeons.api.game.dungeon.subelement.chest

import it.forgottenworld.dungeons.api.game.dungeon.subelement.DungeonSubElement
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.World

interface Chest : Storage.Storable, DungeonSubElement {

    val position: Vector3i

    val minItems: Int

    val maxItems: Int

    val itemChanceMap: Map<Material, Int>

    fun fillActualChest(world: World, position: Vector3i = this.position)

    fun clearActualChest(world: World, position: Vector3i = this.position)
}