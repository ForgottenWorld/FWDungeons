package it.forgottenworld.dungeons.listener

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.NamespacedKeys
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType


class PlayerListener : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent?) {
        val player = event?.entity ?: return
        player.editableDungeon?.onDestroy(true)
        val instance = player.finalInstance ?: return
        instance.onPlayerDeath(player)
        player.sendFWDMessage(Strings.YOU_DIED_IN_THE_DUNGEON)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent?) {
        val player = event?.player ?: return
        player.editableDungeon?.onDestroy(true)
        player.finalInstance?.onPlayerLeave(player)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.player.editableDungeon == null) return

        val persistentDataContainer = event.item?.itemMeta?.persistentDataContainer ?: return

        val isTriggerWand = persistentDataContainer
            .get(NamespacedKeys.TRIGGER_TOOL, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        val isActiveAreaWand = !isTriggerWand && persistentDataContainer
            .get(NamespacedKeys.ACTIVE_AREA_TOOL, PersistentDataType.SHORT)
            ?.toShort() == 1.toShort()

        if (!isTriggerWand && !isActiveAreaWand) return

        val posNo = when (event.action) {
            Action.LEFT_CLICK_BLOCK -> 1
            Action.RIGHT_CLICK_BLOCK -> 2
            else -> return
        }

        val cmd = "fwde ${if (isTriggerWand) "trigger" else "activearea"} pos$posNo"

        event.player.performCommand(cmd)
        event.isCancelled = true
    }
}