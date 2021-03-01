package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import it.forgottenworld.dungeons.api.storage.Storage
import it.forgottenworld.dungeons.api.storage.Storage.Companion.load
import org.bukkit.configuration.ConfigurationSection

class UnlockableSeriesImplStorageStrategy : Storage.StorageStrategy<UnlockableSeries> {

    override fun toConfig(obj: UnlockableSeries, config: ConfigurationSection, storage: Storage) {
        config.set("id", obj.id)
        config.set("name", obj.name)
        config.set("description", obj.description)
        config.createSection("unlockables").run {
            for ((i,unl) in obj.unlockables.withIndex()) {
                storage.save(unl, createSection("$i"))
            }
        }
    }

    override fun fromConfig(config: ConfigurationSection, storage: Storage) = UnlockableSeriesImpl(
        config.getInt("id"),
        config.getString("name")!!,
        config.getString("description")!!,
        config.getConfigurationSection("unlockables")!!.run {
            getKeys(false).map {
                storage.load(getConfigurationSection(it)!!)
            }
        }
    )
}