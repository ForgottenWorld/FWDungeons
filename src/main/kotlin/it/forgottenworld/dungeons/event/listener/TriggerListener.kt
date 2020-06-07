package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.utils.getParty
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent


class TriggerListener: Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent?) {
        val player = event?.player ?: return
        val party = player.getParty() ?: return

        FWDungeonsController.playersTriggering[player.uniqueId]?.let {
            if (!it.isPlayerInside(player))
                it.onPlayerExit(player)
            else return
        }
        if (player.world.name != ConfigManager.dungeonWorld) return

        val trigger = party.instance.triggers.find { it.isPlayerInside(player) } ?: return
        trigger.onPlayerEnter(player)
    }
}