package it.forgottenworld.dungeons.core.game.unlocks

import org.bukkit.configuration.ConfigurationSection

data class UnlockableSeries(
    val id: Int,
    val name: String,
    val description: String,
    val unlockables: List<Unlockable>
) {

    companion object {
        fun fromConfig(config: ConfigurationSection) = UnlockableSeries(
            config.getInt("id"),
            config.getString("name")!!,
            config.getString("description")!!,
            config.getConfigurationSection("unlockables")!!.run {
                getKeys(false).map { uk ->
                    Unlockable.fromConfig(getConfigurationSection(uk)!!)
                }
            }
        )
    }
}