package it.forgottenworld.dungeons.core.game.unlockables

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.storage.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class UnlockableStorageStrategy @Inject constructor(
    private val unlockableFactory: UnlockableFactory
) : Storage.StorageStrategy<Unlockable> {

    override fun toConfig(obj: Unlockable, config: ConfigurationSection, storage: Storage) {
        config.set("seriesId", obj.seriesId)
        config.set("order", obj.order)
        config.set("message", obj.message)
        config.set("unlockedMessage", obj.unlockedMessage)
        config.createSection("requirements").run {
            for (req in obj.requirements) {
                when (req) {
                    is Unlockable.EconomyRequirement -> {
                        set("CURRENCY", req.amount)
                    }
                    is Unlockable.ItemRequirement -> {
                        set(req.material.toString(), req.amount)
                    }
                }
            }
        }
    }

    override fun fromConfig(config: ConfigurationSection, storage: Storage) = unlockableFactory.create(
        config.getInt("seriesId"),
        config.getInt("order"),
        config.getString("message")!!,
        config.getString("unlockedMessage")!!,
        config.getConfigurationSection("requirements")!!.run {
            getKeys(false).map { rk ->
                if (rk == "CURRENCY") {
                    Unlockable.EconomyRequirement(getDouble(rk))
                } else {
                    Unlockable.ItemRequirement(Material.getMaterial(rk)!!, getInt(rk))
                }
            }
        }
    )
}