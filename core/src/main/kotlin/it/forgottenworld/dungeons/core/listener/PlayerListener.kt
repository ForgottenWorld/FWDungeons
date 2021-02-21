package it.forgottenworld.dungeons.core.listener

import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.core.game.instance.DungeonInstanceImpl.Companion.finalInstance
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent


class PlayerListener : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.entity.editableDungeon?.onDestroy(true)
        event.entity.finalInstance?.onPlayerDeath(event.entity)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.editableDungeon?.onDestroy(true)
        event.player.finalInstance?.onPlayerLeave(event.player)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.from.x == event.to.x &&
            event.from.y == event.to.y &&
            event.from.z == event.to.z) return
        event.player.finalInstance?.onPlayerMove(event.player)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        event.player.editableDungeon?.handlePlayerInteract(event)
    }

}