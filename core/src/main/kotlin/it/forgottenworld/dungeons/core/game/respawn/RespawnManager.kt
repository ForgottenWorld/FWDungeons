package it.forgottenworld.dungeons.core.game.respawn

import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.launch
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import kotlinx.coroutines.delay
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

@Singleton
class RespawnManager {

    private val playerRespawnData = mutableMapOf<UUID, RespawnData>()

    fun setPlayerRespawnData(uuid: UUID, respawnData: RespawnData?) {
        if (respawnData != null) {
            playerRespawnData[uuid] = respawnData
        } else {
            playerRespawnData.remove(uuid)
        }
    }

    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val respawnData = playerRespawnData[event.player.uniqueId] ?: return
        event.player.sendPrefixedMessage(Strings.YOU_WILL_BE_TPED_SHORTLY)
        launch {
            delay(1500)
            event.player.teleport(respawnData.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
            event.player.gameMode = respawnData.gameMode
            setPlayerRespawnData(event.player.uniqueId, null)
        }
    }
}