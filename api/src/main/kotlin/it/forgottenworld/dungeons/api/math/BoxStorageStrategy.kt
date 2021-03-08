package it.forgottenworld.dungeons.api.math

import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.configuration.ConfigurationSection

class BoxStorageStrategy : Storage.StorageStrategy<Box> {
    override fun toStorage(
        obj: Box,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit {
            storage.save(obj.origin, section("origin"))
            "width" to obj.width
            "height" to obj.height
            "depth" to obj.depth
        }
    }

    override fun fromStorage(
        config: ConfigurationSection,
        storage: Storage
    ) = config.read {
        Box(
            storage.load(section("origin")!!),
            get("width")!!,
            get("height")!!,
            get("depth")!!
        )
    }
}