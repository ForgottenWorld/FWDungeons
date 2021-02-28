package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.core.config.Storage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class UnlockableImplStorageStrategy : Storage.StorageStrategy<UnlockableImpl> {

    override fun toConfig(obj: UnlockableImpl, config: ConfigurationSection) {
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

    override fun fromConfig(config: ConfigurationSection) = UnlockableImpl(
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