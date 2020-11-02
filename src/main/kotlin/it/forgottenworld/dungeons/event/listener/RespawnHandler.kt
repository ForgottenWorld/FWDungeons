package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.utils.WarpbackData
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.launch
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class RespawnHandler : Listener {

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.player.run {
            respawnData?.let {
                sendFWDMessage("You will be teleported shortly")
                launch {
                    delay(1500)
                    teleport(it.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                    gameMode = it.gameMode
                    respawnData = null
                }
            }
        }
    }

    companion object {

        private val playerRespawnData = mutableMapOf<UUID, WarpbackData>()

        var Player.respawnData
            get() = playerRespawnData[uniqueId]
            set(value) {
                value?.let {
                    playerRespawnData[uniqueId] = it
                } ?: playerRespawnData.remove(uniqueId)
            }
    }
}