package it.forgottenworld.dungeons.core.integrations

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.game.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.echelonapi.FWEchelon
import it.forgottenworld.echelonapi.mutexactivity.MutexActivity
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FWEchelonUtils @Inject constructor(
    private val configuration: Configuration
) {

    fun checkFWEchelonIntegration() {
        val logger = Bukkit.getLogger()

        logger.info("Checking for FWEchelon integration...")
        if (!configuration.fwEchelonIntegration) {
            logger.info("FWEchelon integration is not enabled")
            return
        }

        logger.info("FWEchelon integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("FWEchelon") == null) {
            logger.info("FWEchelon is not present")
            return
        }

        logger.info("FWEchelon is present")

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

    private class FWDungeonsMutexActivity : MutexActivity {

        override val id = MUTEX_ACTIVITY_NAME

        override fun onAllPlayersForceRemoved(reason: String?) {
            val insts = DungeonManager.playerFinalInstances.values
            if (reason != null) {
                for (pl in insts.flatMap { inst -> inst.players.map { Bukkit.getPlayer(it) } }) {
                    pl?.sendFWDMessage(Strings.DUNGEON_WILL_BE_EVACUATED_BECAUSE.format(reason))
                }
            } else {
                for (pl in insts.flatMap { inst -> inst.players.map { Bukkit.getPlayer(it) } }) {
                    pl?.sendFWDMessage(Strings.DUNGEON_WILL_BE_EVACUATED)
                }
            }
            insts.forEach {
                it.evacuate()
            }
        }

        override fun onPlayerForceRemoved(player: Player, reason: String?) {
            val inst = player.uniqueId.finalInstance ?: return
            if (reason != null) {
                player.sendFWDMessage(Strings.YOU_WILL_BE_EVACUATED_BECAUSE.format(reason))
            } else {
                player.sendFWDMessage(Strings.YOU_WILL_BE_EVACUATED)
            }
            inst.rescuePlayer(player)
        }
    }

    companion object {
        const val MUTEX_ACTIVITY_NAME = "FWDungeons"
    }

}