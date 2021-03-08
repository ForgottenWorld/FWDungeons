package it.forgottenworld.dungeons.core.game.dungeon.instance

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.configuration.ConfigurationSection

class DungeonInstanceStorageStrategy @Inject constructor(
    private val dungeonInstanceFactory: DungeonInstanceFactory,
    private val dungeonManager: DungeonManager
) : Storage.StorageStrategy<DungeonInstance> {

    override fun toStorage(
        obj: DungeonInstance,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit {
            "id" to obj.id
            "dId" to obj.dungeon.id
            storage.save(obj.origin, section("origin"))
        }
    }

    override fun fromStorage(
        config: ConfigurationSection,
        storage: Storage
    ) = config.read {
        dungeonInstanceFactory.create(
            get("id")!!,
            dungeonManager.getFinalDungeonById(get("dId")!!)!!,
            storage.load(section("origin")!!)
        )
    }
}