package it.forgottenworld.dungeons.core.integrations

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendConsoleMessage
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import it.forgottenworld.echelonapi.FWEchelon
import it.forgottenworld.echelonapi.mutexactivity.MutexActivity
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FWEchelonUtils @Inject constructor(
    private val configuration: Configuration,
    private val dungeonManager: DungeonManager
) {

    fun checkFWEchelonIntegration() {
        sendConsoleMessage("${Strings.CONSOLE_PREFIX}Checking for FWEchelon integration...")
        if (!configuration.fwEchelonIntegration) {
            sendConsoleMessage(" -- FWEchelon integration is §4not enabled")
            return
        }

        sendConsoleMessage(" -- FWEchelon integration §2is enabled")
        if (Bukkit.getPluginManager().getPlugin("FWEchelon") == null) {
            sendConsoleMessage(" -- FWEchelon is §4not present")
            return
        }

        sendConsoleMessage(" -- FWEchelon §2is present")

        FWEchelon.api
            .mutexActivityService
            .registerMutexActivity(FWDungeonsMutexActivity())

        configuration.useFWEchelon = true
    }

    fun isPlayerFree(player: Player) = !configuration.useFWEchelon ||
        !FWEchelon.api.mutexActivityService.isPlayerInMutexActivity(player)

    fun playerIsNoLongerFree(player: Player) {
        if (!configuration.useFWEchelon) return
        FWEchelon
            .api
            .mutexActivityService
            .playerJoinMutexActivity(
                player,
                MUTEX_ACTIVITY_NAME,
                true
            )
    }

    fun playerIsNowFree(player: Player) {
        if (!configuration.useFWEchelon) return
        FWEchelon
            .api
            .mutexActivityService
            .removePlayerFromMutexActivity(
                player,
                MUTEX_ACTIVITY_NAME
            )
    }

    private inner class FWDungeonsMutexActivity : MutexActivity {

        override val id = MUTEX_ACTIVITY_NAME

        override fun onAllPlayersForceRemoved(reason: String?) {
            val insts = dungeonManager.getAllBusyInstances()
            if (reason != null) {
                for (pl in insts.flatMap { inst -> inst.players.map { Bukkit.getPlayer(it) } }) {
                    pl?.sendPrefixedMessage(Strings.DUNGEON_WILL_BE_EVACUATED_BECAUSE.format(reason))
                }
            } else {
                for (pl in insts.flatMap { inst -> inst.players.map { Bukkit.getPlayer(it) } }) {
                    pl?.sendPrefixedMessage(Strings.DUNGEON_WILL_BE_EVACUATED)
                }
            }
            insts.forEach {
                it.evacuate()
            }
        }

        override fun onPlayerForceRemoved(player: Player, reason: String?) {
            val inst = dungeonManager.getPlayerInstance(player.uniqueId) ?: return
            if (reason != null) {
                player.sendPrefixedMessage(Strings.YOU_WILL_BE_EVACUATED_BECAUSE.format(reason))
            } else {
                player.sendPrefixedMessage(Strings.YOU_WILL_BE_EVACUATED)
            }
            inst.rescuePlayer(player)
        }
    }

    companion object {
        const val MUTEX_ACTIVITY_NAME = "FWDungeons"
    }
}