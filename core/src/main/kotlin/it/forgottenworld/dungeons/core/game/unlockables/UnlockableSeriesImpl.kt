package it.forgottenworld.dungeons.core.game.unlockables

import it.forgottenworld.dungeons.api.game.unlockables.Unlockable
import it.forgottenworld.dungeons.api.game.unlockables.UnlockableSeries
import org.bukkit.configuration.ConfigurationSection

data class UnlockableSeriesImpl(
    override val id: Int,
    override val name: String,
    override val description: String,
    override val unlockables: List<Unlockable>
) : UnlockableSeries {

    fun toConfig(config: ConfigurationSection) {
        config.set("id", id)
        config.set("name", name)
        config.set("description", description)
        config.createSection("unlockables").run {
            for ((i,unl) in unlockables.withIndex()) {
                (unl as UnlockableImpl).toConfig(createSection("$i"))
            }
        }
    }

    companion object {
        fun fromConfig(config: ConfigurationSection) = UnlockableSeriesImpl(
            config.getInt("id"),
            config.getString("name")!!,
            config.getString("description")!!,
            config.getConfigurationSection("unlockables")!!.run {
                getKeys(false).map {
                    UnlockableImpl.fromConfig(getConfigurationSection(it)!!)
                }
            }
        )
    }
}