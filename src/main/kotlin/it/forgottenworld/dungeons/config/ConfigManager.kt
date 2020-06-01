package it.forgottenworld.dungeons.config

import org.bukkit.configuration.file.FileConfiguration

object ConfigManager {
    lateinit var config: FileConfiguration

    val isInDebugMode: Boolean by lazy { config.getBoolean("debugMode") }
    val dungeonWorld: String by lazy { config.getString("dungeonWorld")!! }

    fun loadConfig(config: FileConfiguration) {
        this.config = config
    }
}