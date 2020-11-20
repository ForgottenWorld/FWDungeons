package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.Strings
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
        val player = event.player
        player.respawnData?.let {
            player.sendFWDMessage(Strings.YOU_WILL_BE_TPED_SHORTLY)
            launch {
                delay(1500)
                player.teleport(it.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                player.gameMode = it.gameMode
                player.respawnData = null
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