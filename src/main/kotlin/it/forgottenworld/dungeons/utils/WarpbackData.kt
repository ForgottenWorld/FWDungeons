package it.forgottenworld.dungeons.utils

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.*

data class WarpbackData(val gameMode: GameMode, val worldId: UUID, val position: Vector) {
    val location
        get() = Location(Bukkit.getWorld(worldId), position.x, position.y, position.z)
}