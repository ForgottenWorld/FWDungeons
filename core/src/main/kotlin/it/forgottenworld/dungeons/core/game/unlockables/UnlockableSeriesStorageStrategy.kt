package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import it.forgottenworld.dungeons.api.storage.Storage.Companion.save
import it.forgottenworld.dungeons.api.storage.edit
import it.forgottenworld.dungeons.api.storage.read
import org.bukkit.configuration.ConfigurationSection

class UnlockableSeriesStorageStrategy : Storage.StorageStrategy<UnlockableSeries> {

    override fun toStorage(
        obj: UnlockableSeries,
        config: ConfigurationSection,
        storage: Storage
    ) {
        config.edit {
            "id" to obj.id
            "name" to obj.name
            "description" to obj.description
            section("unlockables") {
                for ((i, unl) in obj.unlockables.withIndex()) {
                    storage.save(unl, section("$i"))
                }
            }
        }
    }

    override fun fromStorage(
        config: ConfigurationSection,
        storage: Storage
    ) = config.read {
        UnlockableSeriesImpl(
            get("id")!!,
            get("name")!!,
            get("description")!!,
            section("unlockables") {
                mapSections { _, sec -> storage.load(sec) }
            }!!
        )
    }
}