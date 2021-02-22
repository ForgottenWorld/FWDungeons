package it.forgottenworld.dungeons.api.game.chest

import it.forgottenworld.dungeons.api.math.Vector3i
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack

interface Chest {
    val id: Int
    val position: Vector3i
    val label: String?
    val itemAmountRange: IntRange
    val itemChanceMap: Map<Material, Int>
    val items: Array<ItemStack>
    fun fillActualChest(world: World)
    fun clearActualChest(world: World)
}