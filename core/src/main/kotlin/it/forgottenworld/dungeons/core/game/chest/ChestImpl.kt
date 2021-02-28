package it.forgottenworld.dungeons.core.game.chest

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Storage
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import kotlin.random.Random
import org.bukkit.block.Chest as ChestBlock

data class ChestImpl(
    override val id: Int,
    override val position: Vector3i,
    override var label: String? = null,
    override val minItems: Int = 1,
    override val maxItems: Int = 4,
    override val itemChanceMap: Map<Material, Int> = mapOf()
) : Chest, Storage.Storable {

    override val items: Array<ItemStack>

    init {
        val hay = itemChanceMap.values.sum()
        items = (0 until Random.nextInt(minItems, maxItems + 1)).map {
            val rng = Random.nextInt(hay)
            var acc = 0
            itemChanceMap
                .toList()
                .find { (_,v) ->
                    acc += v
                    acc > rng
                }!!
                .first
                .let(::ItemStack)
        }.toTypedArray()
    }

    override fun fillActualChest(world: World, position: Vector3i) {
        val block = position.blockInWorld(world)
        val chest = block.state as? ChestBlock ?: return
        chest.inventory.contents = items
    }

    override fun clearActualChest(world: World, position: Vector3i) {
        val block = position.blockInWorld(world)
        val chest = block.state as? ChestBlock ?: return
        chest.inventory.clear()
    }
}