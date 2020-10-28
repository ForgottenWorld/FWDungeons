package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.manager.DungeonManager.collidingTrigger
import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TriggerListener: Listener {

    @EventHandler
    fun onTriggerAsync(event: TriggerEvent) {
        val player = Bukkit.getPlayer(event.playerUuid) ?: return

        if (event.erase) player.collidingTrigger?.onPlayerExit(player)

        if (event.triggerId == -1) return

        val inst = player.dungeonInstance ?: return
        inst.triggers[event.triggerId]?.onPlayerEnter(player, inst)
    }
}