package it.forgottenworld.dungeons.core.utils

import it.forgottenworld.dungeons.api.math.Vector3i
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

data class WarpbackData(
    val gameMode: GameMode,
    val worldId: UUID,
    val position: Vector3i
) {
    val location
        get() = Location(
            Bukkit.getWorld(worldId),
            position.x.toDouble(),
            position.y.toDouble(),
            position.z.toDouble()
        )

    fun useWithPlayer(player: Player) {
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
        player.gameMode = gameMode
    }

    companion object {
        val Player.currentWarpbackData get() = WarpbackData(
            gameMode,
            location.world.uid,
            location.toVector3i()
        )
    }
}