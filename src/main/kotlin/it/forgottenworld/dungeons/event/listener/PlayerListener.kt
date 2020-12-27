package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.ktx.plugin
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType


class PlayerListener: Listener {

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
    fun onPlayerTeleport(event: PlayerTeleportEvent?) {
        val player = event?.player ?: return
        if (player.finalInstance?.isTpSafe != false) return

        when (event.cause) {
            PlayerTeleportEvent.TeleportCause.ENDER_PEARL, PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT -> {
                player.sendFWDMessage(Strings.NO_EPEARLS_OR_CHORUS_FRUIT_ALLOWED)
                event.isCancelled = true
                return
            }
            PlayerTeleportEvent.TeleportCause.COMMAND, PlayerTeleportEvent.TeleportCause.PLUGIN -> {
                player.sendFWDMessage(Strings.YOU_WISH_YOU_COULD)
                player.damage(2.0)
                event.isCancelled = true
            }
            else -> return
        }

    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.player.finalInstance != null &&
                event.item?.type == Material.ENDER_PEARL &&
                (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) {
            event.player.sendFWDMessage(Strings.NO_EPEARLS_IN_THE_DUNGEON)
            event.isCancelled = true
            return
        }

        if (event.player.editableDungeon == null) return

        val persistentDataContainer = event.item?.itemMeta?.persistentDataContainer ?: return
        val isTriggerWand =
                persistentDataContainer
                        .get(NamespacedKey(plugin, "FWD_TRIGGER_WAND"), PersistentDataType.SHORT)
                        ?.toShort()
                        ?.equals(1.toShort()) ?: false
        val isActiveAreaWand = !isTriggerWand
                && persistentDataContainer
                        .get(NamespacedKey(plugin, "FWD_ACTIVE_AREA_WAND"), PersistentDataType.SHORT)
                        ?.toShort()
                        ?.equals(1.toShort()) ?: false

        if (!isTriggerWand && !isActiveAreaWand) return
        val cmd = when (event.action) {
            Action.LEFT_CLICK_BLOCK ->  "fwde ${if (isTriggerWand) "trigger" else "activearea"} pos1"
            Action.RIGHT_CLICK_BLOCK -> "fwde ${if (isTriggerWand) "trigger" else "activearea"} pos2"
            else -> return
        }
        event.player.performCommand(cmd)
        event.isCancelled = true
    }
}