package it.forgottenworld.dungeons.game.chest

import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockVector
import kotlin.random.Random
import org.bukkit.block.Chest as ChestBlock

data class Chest(
    val id: Int,
    val position: BlockVector,
    val itemAmountRange: IntRange,
    val itemChanceMap: Map<Material, Int>
) {

    private val items: Array<ItemStack>

    init {
        val hay = itemChanceMap.values.sum()
        items = (0 until Random.nextInt(
            itemAmountRange.first,
            itemAmountRange.last + 1
        )).map {
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

    fun fillChestBlock(chest: ChestBlock) {
        chest.inventory.contents = items
    }

    fun toConfig(config: ConfigurationSection) {
        config.set("id", id)
        config.set("position", position.toVector())
        config.set("minItems", itemAmountRange.first)
        config.set("maxItems", itemAmountRange.last)
        config.createSection("chances").run {
            for ((k,v) in itemChanceMap.entries) {
                set(k.toString(), v)
            }
        }
    }

    companion object {

        fun fromConfig(config: ConfigurationSection): Chest {
            val id = config.getInt("id")
            val position = config.getVector("position")!!.toBlockVector()
            val minItems = config.getInt("minItems")
            val maxItems = config.getInt("maxItems")
            val itemAmountRange = minItems..maxItems
            val chancesConfig = config.getConfigurationSection("chances")!!
            val chancesMap = chancesConfig
                .getKeys(false)
                .associate { Material.valueOf(it) to chancesConfig.getInt(it) }
            return Chest(id,position,itemAmountRange,chancesMap)
        }
    }
}