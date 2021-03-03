package it.forgottenworld.dungeons.core.game.detection

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffectType

@Singleton
class BypassAttemptHandler @Inject constructor(
    private val dungeonManager: DungeonManager
) {

    fun onPlayerTeleport(event: PlayerTeleportEvent) {

        fun preventTp(because: String) {
            event.player.sendFWDMessage(because)
            event.isCancelled = true
        }

        when (event.cause) {
            PlayerTeleportEvent.TeleportCause.ENDER_PEARL,
            PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT -> {
                preventTp(Strings.NO_EPEARLS_OR_CHORUS_FRUIT_ALLOWED)
            }
            PlayerTeleportEvent.TeleportCause.COMMAND,
            PlayerTeleportEvent.TeleportCause.PLUGIN -> {
                preventTp(Strings.YOU_WISH_YOU_COULD)
            }
            else -> return
        }
    }

    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        val player = event.entity as? Player ?: return
        if (dungeonManager.getPlayerInstance(player.uniqueId) != null &&
            checkedCauses.contains(event.cause) &&
            bannedPotionEffects.contains(event.modifiedType)
        ) {
            player.sendFWDMessage(Strings.POTION_EFFECT_NOT_ALLOWED)
            event.isCancelled = true
        }
    }

    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (dungeonManager.getPlayerInstance(event.player.uniqueId) == null ||
            event.item?.type != Material.ENDER_PEARL ||
            !(event.action == Action.RIGHT_CLICK_AIR ||
                event.action == Action.RIGHT_CLICK_BLOCK)) return
        event.player.sendFWDMessage(Strings.NO_EPEARLS_IN_THE_DUNGEON)
        event.isCancelled = true
    }

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