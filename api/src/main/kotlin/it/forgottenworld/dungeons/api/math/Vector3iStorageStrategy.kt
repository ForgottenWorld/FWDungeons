package it.forgottenworld.dungeons.api.math

import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.serialization.edit
import it.forgottenworld.dungeons.api.serialization.read
import org.bukkit.configuration.ConfigurationSection

class Vector3iStorageStrategy : Storage.StorageStrategy<Vector3i> {
    override fun toStorage(
        obj: Vector3i,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit {
            "x" to obj.x
            "y" to obj.y
            "z" to obj.z
        }
    }

    override fun fromStorage(
        config: ConfigurationSection,
        storage: Storage
    ) = config.read {
        Vector3i(get("x")!!,get("y")!!,get("z")!!)
    }
}