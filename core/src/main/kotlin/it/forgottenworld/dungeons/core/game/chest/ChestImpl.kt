package it.forgottenworld.dungeons.core.game.chest

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.math.getBlockAt
import it.forgottenworld.dungeons.api.math.toVector
import it.forgottenworld.dungeons.api.math.toVector3i
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import kotlin.random.Random
import org.bukkit.block.Chest as ChestBlock

data class ChestImpl(
    override val id: Int,
    override val position: Vector3i,
    override var label: String? = null,
    override val itemAmountRange: IntRange = 1..4,
    override val itemChanceMap: Map<Material, Int> = mapOf()
) : Chest {

    override val items: Array<ItemStack>

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

    override fun fillActualChest(world: World, position: Vector3i) {
        val block = world.getBlockAt(position)
        val chest = block.state as? ChestBlock ?: return
        chest.inventory.contents = items
    }

    override fun clearActualChest(world: World, position: Vector3i) {
        val block = world.getBlockAt(position)
        val chest = block.state as? ChestBlock ?: return
        chest.inventory.clear()
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

        fun fromConfig(config: ConfigurationSection): ChestImpl {
            val id = config.getInt("id")
            val position = config.getVector("position")!!.toVector3i()
            val label = config.getString("label")
            val minItems = config.getInt("minItems")
            val maxItems = config.getInt("maxItems")
            val itemAmountRange = minItems..maxItems
            val chancesConfig = config.getConfigurationSection("chances")!!
            val chancesMap = chancesConfig
                .getKeys(false)
                .associate { Material.valueOf(it) to chancesConfig.getInt(it) }
            return ChestImpl(id,position,label,itemAmountRange,chancesMap)
        }
    }
}