package it.forgottenworld.dungeons.core.game.dungeon.instance

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import org.bukkit.configuration.ConfigurationSection

class DungeonInstanceStorageStrategy @Inject constructor(
    private val dungeonInstanceFactory: DungeonInstanceFactory,
    private val dungeonManager: DungeonManager
) : Storage.StorageStrategy<DungeonInstance> {

    override fun toStorage(obj: DungeonInstance, config: ConfigurationSection, storage: Storage) {
        config.set("id", obj.id)
        config.set("dId", obj.dungeon.id)
        config.set("x", obj.origin.x)
        config.set("y", obj.origin.y)
        config.set("z", obj.origin.z)
    }

    override fun fromStorage(config: ConfigurationSection, storage: Storage) = dungeonInstanceFactory.create(
        config.getInt("id"),
        dungeonManager.getFinalDungeonById(config.getInt(("dId")))!!,
        Vector3i(config.getInt("x"),config.getInt("y"),config.getInt("z"))
    )
}