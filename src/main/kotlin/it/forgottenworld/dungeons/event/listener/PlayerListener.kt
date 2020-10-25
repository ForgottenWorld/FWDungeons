package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.manager.DungeonEditManager.isEditingDungeon
import it.forgottenworld.dungeons.manager.DungeonManager.dungeonInstance
import it.forgottenworld.dungeons.manager.DungeonManager.party
import it.forgottenworld.dungeons.manager.RespawnManager.respawnGameMode
import it.forgottenworld.dungeons.manager.RespawnManager.respawnLocation
import it.forgottenworld.dungeons.utils.bukkitThreadLater
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType


class PlayerListener: Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent?) {
        val player = event?.entity ?: return
        val party = player.party ?: return

        party.playerDied(player)
        player.sendFWDMessage("${ChatColor.RED}You died in the dungeon")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent?) {
        val player = event?.player ?: return

        DungeonEditManager.purgeWorkingData(player)

        player.party ?: return

        player.health = 0.0
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent?) {
        val player = event?.player ?: return
        player.party ?: return
        if (player.dungeonInstance?.isTpSafe == true) return

        when (event.cause) {
            PlayerTeleportEvent.TeleportCause.ENDER_PEARL, PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT -> {
                player.sendFWDMessage("No ender pearls or chorus fruits in the dungeon!")
                event.isCancelled = true
                return
            }
            PlayerTeleportEvent.TeleportCause.COMMAND, PlayerTeleportEvent.TeleportCause.PLUGIN -> {
                player.sendFWDMessage("You wish you could!")
                player.damage(2.0)
                event.isCancelled = true
            }
            else -> return
        }

    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!event.player.isEditingDungeon) return

        val persistentDataContainer = event.player.inventory.itemInMainHand.itemMeta?.persistentDataContainer ?: return
        val isTriggerWand =
                persistentDataContainer
                        .get(NamespacedKey(FWDungeonsPlugin.instance, "FWD_TRIGGER_WAND"), PersistentDataType.SHORT)
                        ?.toShort()
                        ?.equals(1.toShort()) ?: false
        val isActiveAreaWand = !isTriggerWand
                && persistentDataContainer
                        .get(NamespacedKey(FWDungeonsPlugin.instance, "FWD_ACTIVE_AREA_WAND"), PersistentDataType.SHORT)
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

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.player.run {
            respawnLocation?.let {
                sendFWDMessage("You will be teleported shortly")
                bukkitThreadLater(30L) {
                    teleport(it, PlayerTeleportEvent.TeleportCause.PLUGIN)
                    respawnLocation = null
                }
            }
            respawnGameMode?.let {
                gameMode = it
                respawnGameMode = null
            }
        }
    }
}