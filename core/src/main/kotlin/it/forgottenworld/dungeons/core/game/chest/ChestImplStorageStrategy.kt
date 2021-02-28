package it.forgottenworld.dungeons.core.game.chest

import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ChestImplStorageStrategy : Storage.StorageStrategy<ChestImpl> {

    override fun toConfig(obj: ChestImpl, config: ConfigurationSection) {
        config.set("id", obj.id)
        config.set("position", obj.position.toVector())
        config.set("minItems", obj.minItems)
        config.set("maxItems", obj.maxItems)
        config.createSection("chances").run {
            for ((k,v) in obj.itemChanceMap.entries) {
                set(k.toString(), v)
            }
        }
    }

    override fun fromConfig(config: ConfigurationSection): ChestImpl {
        val id = config.getInt("id")
        val position = Vector3i.ofBukkitVector(config.getVector("position")!!)
        val label = config.getString("label")
        val minItems = config.getInt("minItems")
        val maxItems = config.getInt("maxItems")
        val chancesConfig = config.getConfigurationSection("chances")!!
        val chancesMap = chancesConfig
            .getKeys(false)
            .associate { Material.valueOf(it) to chancesConfig.getInt(it) }
        return ChestImpl(id, position, label, minItems, maxItems, chancesMap)
    }
}