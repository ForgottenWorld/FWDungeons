package it.forgottenworld.dungeons.api.game.chest

import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack

interface Chest : Storage.Storable {
    val id: Int
    val position: Vector3i
    var label: String?
    val minItems: Int
    val maxItems: Int
    val itemChanceMap: Map<Material, Int>
    val items: Array<ItemStack>
    fun fillActualChest(world: World, position: Vector3i = this.position)
    fun clearActualChest(world: World, position: Vector3i = this.position)
}