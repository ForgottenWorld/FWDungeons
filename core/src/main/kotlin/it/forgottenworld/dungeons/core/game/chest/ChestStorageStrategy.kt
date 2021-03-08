package it.forgottenworld.dungeons.core.game.chest

import it.forgottenworld.dungeons.api.game.chest.Chest
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ChestStorageStrategy : Storage.StorageStrategy<Chest> {

    override fun toStorage(obj: Chest, config: ConfigurationSection, storage: Storage) {
        config.edit {
            "id" to obj.id
            storage.save(
                obj.position,
                section("position")
            )
            "minItems" to obj.minItems
            "maxItems" to obj.maxItems
            section("chances") {
                for ((k,v) in obj.itemChanceMap.entries) {
                    "$k" to v
                }
            }
        }
    }

    override fun fromStorage(config: ConfigurationSection, storage: Storage) = config.read {
        ChestImpl(
            get("id")!!,
            storage.load(section("position")!!),
            get("label"),
            get("minItems")!!,
            get("maxItems")!!,
            section("chances") { toMap(Material::valueOf) }!!
        )
    }
}