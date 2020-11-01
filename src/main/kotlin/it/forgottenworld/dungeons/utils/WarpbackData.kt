package it.forgottenworld.dungeons.utils

import it.forgottenworld.dungeons.config.ConfigManager
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.util.Vector

data class WarpbackData(val gameMode: GameMode, val position: Vector) {
    val location
        get() = Location(ConfigManager.dungeonWorld, position.x, position.y, position.z)
}