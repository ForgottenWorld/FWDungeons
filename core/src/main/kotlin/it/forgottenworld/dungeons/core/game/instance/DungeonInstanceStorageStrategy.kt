package it.forgottenworld.dungeons.core.game.instance

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.instance.DungeonInstance
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.core.game.DungeonManager
import org.bukkit.configuration.ConfigurationSection

class DungeonInstanceStorageStrategy @Inject constructor(
    private val dungeonInstanceFactory: DungeonInstanceFactory
) : Storage.StorageStrategy<DungeonInstance> {

    override fun toConfig(obj: DungeonInstance, config: ConfigurationSection, storage: Storage) {
        config.set("id", obj.id)
        config.set("dId", obj.dungeon.id)
        config.set("x", obj.origin.x)
        config.set("y", obj.origin.y)
        config.set("z", obj.origin.z)
    }

    override fun fromConfig(config: ConfigurationSection, storage: Storage) = dungeonInstanceFactory.create(
        config.getInt("id"),
        DungeonManager.finalDungeons[config.getInt(("dId"))]!!,
        Vector3i(config.getInt("x"),config.getInt("y"),config.getInt("z"))
    )
}