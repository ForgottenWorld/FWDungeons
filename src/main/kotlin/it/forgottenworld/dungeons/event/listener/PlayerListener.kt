package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.controller.FWDungeonsController
import it.forgottenworld.dungeons.utils.getParty
import net.md_5.bungee.api.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent


class PlayerListener: Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent?) {
        val player = event?.entity ?: return
        val party = player.getParty() ?: return

        party.playerDied(player)
        player.sendMessage("${ChatColor.RED}You died in the dungeon")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent?) {
        val player = event?.player ?: return
        player.getParty() ?: return

        player.health = 0.0
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent?) {
        val player = event?.player ?: return
        player.getParty() ?: return

        if (event.cause == PlayerTeleportEvent.TeleportCause.COMMAND) {
            player.sendMessage("You wish you could!")
            player.damage(2.0)
            event.isCancelled = true
        }
    }
}