package it.forgottenworld.dungeons.core.game

import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.WarpbackData
import it.forgottenworld.dungeons.core.utils.launch
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

object RespawnHandler {

    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val respawnData = event.player.respawnData ?: return
        event.player.sendFWDMessage(Strings.YOU_WILL_BE_TPED_SHORTLY)
        launch {
            delay(1500)
            event.player.teleport(respawnData.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
            event.player.gameMode = respawnData.gameMode
            event.player.respawnData = null
        }
    }

    private val playerRespawnData = mutableMapOf<UUID, WarpbackData>()

    var Player.respawnData
        get() = playerRespawnData[uniqueId]
        set(value) {
            value?.let {
                playerRespawnData[uniqueId] = it
            } ?: playerRespawnData.remove(uniqueId)
        }
}