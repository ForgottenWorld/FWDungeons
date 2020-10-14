package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.state.DungeonState.collidingTrigger
import it.forgottenworld.dungeons.state.DungeonState.party
import it.forgottenworld.dungeons.utils.eyeBlock
import it.forgottenworld.dungeons.utils.footBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent


class TriggerListener: Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent?) {
        val player = event?.player ?: return

        val loc = player.location
        if (event.from.x == loc.x
                && event.from.y == loc.y
                && event.from.z == loc.z) return

        val wipInstance = DungeonEditState.wipTestInstances[player.uniqueId]
        val party = player.party ?: return

        if (wipInstance == null || !party.inGame) return

        player.collidingTrigger?.let {
            if (!it.isPlayerInside(player))
                it.onPlayerExit(player)
            else return
        }

        val world = player.world
        if (world.name != ConfigManager.dungeonWorld) return

        // wipInstance?.triggers?.values?.find { it.isPlayerInside(player) }?.onPlayerEnter(player)
        // party?.instance?.triggers?.find { it.isPlayerInside(player) }?.onPlayerEnter(player)

        (player.footBlock
                .getMetadata("FWD_triggers")
                .firstOrNull()
                ?: player.eyeBlock
                        .getMetadata("FWD_triggers")
                        .firstOrNull()
                )
                ?.asString()
                ?.substringBefore('-', "")
                ?.takeIf { it.isNotEmpty() }
                ?.let {
                    wipInstance.triggers[it.toInt()]?.onPlayerEnter(player)
                    party.instance.triggers[it.toInt()]?.onPlayerEnter(player)
                }
    }
}