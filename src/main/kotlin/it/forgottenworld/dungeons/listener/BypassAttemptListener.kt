package it.forgottenworld.dungeons.listener

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffectType

class BypassAttemptListener : Listener {

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent?) {
        val player = event?.player ?: return
        if (player.finalInstance?.isTpSafe != false) return

        fun preventTp(because: String, damage: Double = 0.0) {
            player.sendFWDMessage(because)
            if (damage != 0.0) player.damage(damage)
            event.isCancelled = true
        }

        when (event.cause) {
            PlayerTeleportEvent.TeleportCause.ENDER_PEARL,
            PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT -> {
                preventTp(Strings.NO_EPEARLS_OR_CHORUS_FRUIT_ALLOWED)
            }
            PlayerTeleportEvent.TeleportCause.COMMAND,
            PlayerTeleportEvent.TeleportCause.PLUGIN -> {
                preventTp(Strings.YOU_WISH_YOU_COULD, 2.0)
            }
            else -> return
        }
    }

    @EventHandler
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        val player = event.entity as? Player ?: return
        if (player.finalInstance != null &&
            checkedCauses.contains(event.cause) &&
            bannedPotionEffects.contains(event.modifiedType)
        ) {
            player.sendFWDMessage(Strings.POTION_EFFECT_NOT_ALLOWED)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.player.finalInstance == null ||
            event.item?.type != Material.ENDER_PEARL ||
            !(event.action == Action.RIGHT_CLICK_AIR ||
                event.action == Action.RIGHT_CLICK_BLOCK)) return
        event.player.sendFWDMessage(Strings.NO_EPEARLS_IN_THE_DUNGEON)
        event.isCancelled = true
    }

    companion object {
        private val bannedPotionEffects = setOf(
            PotionEffectType.JUMP,
            PotionEffectType.SPEED,
            PotionEffectType.LEVITATION
        )
        private val checkedCauses = setOf(
            EntityPotionEffectEvent.Cause.POTION_DRINK,
            EntityPotionEffectEvent.Cause.POTION_SPLASH,
            EntityPotionEffectEvent.Cause.ARROW,
            EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD
        )
    }
}