package it.forgottenworld.dungeons.core.integrations

import it.forgottenworld.dungeons.core.config.ConfigManager
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl.Companion.finalInstance
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import it.forgottenworld.echelonapi.FWEchelon
import it.forgottenworld.echelonapi.mutexactivity.MutexActivity
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object FWEchelonUtils {

    const val MUTEX_ACTIVITY_NAME = "FWDungeons"

    fun checkFWEchelonIntegration() {
        val logger = Bukkit.getLogger()
        logger.info("Checking for FWEchelon integration...")
        if (!ConfigManager.fwEchelonIntegration) {
            logger.info("FWEchelon integration is not enabled")
            return
        }

        logger.info("FWEchelon integration is enabled")
        if (Bukkit.getPluginManager().getPlugin("FWEchelon") == null) {
            logger.info("FWEchelon is not present")
            return
        }

        logger.info("FWEchelon is present")
        registerMutexActivity()
        ConfigManager.useFWEchelon = true
    }

    private fun registerMutexActivity() {
        FWEchelon.api.mutexActivityService.registerMutexActivity(FWDungeonsMutexActivity())
    }

    fun isPlayerFree(player: Player) = !ConfigManager.useFWEchelon ||
        !FWEchelon.api.mutexActivityService.isPlayerInMutexActivity(player)

    fun playerIsNoLongerFree(player: Player) {
        if (!ConfigManager.useFWEchelon) return
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
        if (!ConfigManager.useFWEchelon) return
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
            val insts = DungeonInstanceImpl.finalInstances.values
            if (reason != null) {
                insts.flatMap { it.players }.forEach {
                    it?.sendFWDMessage(Strings.DUNGEON_WILL_BE_EVACUATED_BECAUSE.format(reason))
                }
            } else {
                insts.flatMap { it.players }.forEach {
                    it?.sendFWDMessage(Strings.DUNGEON_WILL_BE_EVACUATED)
                }
            }
            insts.forEach {
                it.evacuate()
            }
        }

        override fun onPlayerForceRemoved(player: Player, reason: String?) {
            val inst = player.finalInstance ?: return
            if (reason != null) {
                player.sendFWDMessage(Strings.YOU_WILL_BE_EVACUATED_BECAUSE.format(reason))
            } else {
                player.sendFWDMessage(Strings.YOU_WILL_BE_EVACUATED)
            }
            inst.rescuePlayer(player)
        }
    }

}