package it.forgottenworld.dungeons.core.game.chest

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ChestStorageStrategy : Storage.StorageStrategy<Chest> {

    override fun toStorage(obj: Chest, config: ConfigurationSection, storage: Storage) {
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

    override fun fromStorage(config: ConfigurationSection, storage: Storage): ChestImpl {
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