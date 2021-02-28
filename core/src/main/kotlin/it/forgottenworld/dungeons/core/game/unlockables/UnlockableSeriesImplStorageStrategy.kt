package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.core.config.Storage
import it.forgottenworld.dungeons.core.config.Storage.toConfig
import org.bukkit.configuration.ConfigurationSection

class UnlockableSeriesImplStorageStrategy : Storage.StorageStrategy<UnlockableSeriesImpl> {

    override fun toConfig(obj: UnlockableSeriesImpl, config: ConfigurationSection) {
        config.set("id", obj.id)
        config.set("name", obj.name)
        config.set("description", obj.description)
        config.createSection("unlockables").run {
            for ((i,unl) in obj.unlockables.withIndex()) {
                (unl as UnlockableImpl).toConfig(createSection("$i"))
            }
        }
    }

    override fun fromConfig(config: ConfigurationSection) = UnlockableSeriesImpl(
        config.getInt("id"),
        config.getString("name")!!,
        config.getString("description")!!,
        config.getConfigurationSection("unlockables")!!.run {
            getKeys(false).map {
                Storage.load<UnlockableImpl>(getConfigurationSection(it)!!)
            }
        }
    )
}