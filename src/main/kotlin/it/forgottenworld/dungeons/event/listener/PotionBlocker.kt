package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.potion.PotionEffectType

class PotionBlocker : Listener {

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

    @EventHandler
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        val player = event.entity as? Player ?: return
        if (player.finalInstance == null) return
        if (checkedCauses.contains(event.cause) &&
            bannedPotionEffects.contains(event.modifiedType)) {
                player.sendFWDMessage(Strings.POTION_EFFECT_NOT_ALLOWED)
            event.isCancelled = true
        }
    }
}