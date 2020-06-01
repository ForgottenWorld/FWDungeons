package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.controller.FWDungeonsController
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent


class TriggerListener : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent?) {
        event ?: return

        val player = event.player

        if (player.world.name == ConfigManager.dungeonWorld) {
            FWDungeonsController.playersTriggering[player.uniqueId]?.let {
                if (!it.isPlayerTriggering(player))
                    it.onPlayerExit(player)
            }
            ?: FWDungeonsController.dungeons.values.flatMap { it.instances }.find {
                it.box.containsPlayer(player)
            }?.triggers
                    ?.find {
                        it.isPlayerTriggering(player)
                    }?.onPlayerEnter(player)
        }
    }
}