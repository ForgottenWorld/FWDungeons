package it.forgottenworld.dungeons.core.game.unlockables

import org.bukkit.configuration.ConfigurationSection

data class UnlockableSeries(
    val id: Int,
    val name: String,
    val description: String,
    val unlockables: List<Unlockable>
) {

    fun toConfig(config: ConfigurationSection) {
        config.set("id", id)
        config.set("name", name)
        config.set("description", description)
        config.createSection("unlockables").run {
            for ((i,unl) in unlockables.withIndex()) {
                unl.toConfig(createSection("$i"))
            }
        }
    }

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