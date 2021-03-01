package it.forgottenworld.dungeons.core

import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.game.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.game.RespawnManager
import it.forgottenworld.dungeons.core.game.detection.BypassAttemptHandler
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager.combatObjective
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*


class SpigotEventDispatcher : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.entity.uniqueId.editableDungeon?.onDestroy(true)
        event.entity.uniqueId.finalInstance?.onPlayerDeath(event.entity)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.uniqueId.editableDungeon?.onDestroy(true)
        event.player.uniqueId.finalInstance?.onPlayerLeave(event.player)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.from.x == event.to.x &&
            event.from.y == event.to.y &&
            event.from.z == event.to.z
        ) return
        event.player.uniqueId.finalInstance?.onPlayerMove(event.player)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        event.player.uniqueId.editableDungeon?.onPlayerInteract(event)
        BypassAttemptHandler.onPlayerInteract(event)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        event.entity.uniqueId.combatObjective?.onMobKilled(event.entity.uniqueId)
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.player.uniqueId.finalInstance?.isTpSafe != false) return
        BypassAttemptHandler.onPlayerTeleport(event)
    }

    @EventHandler
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        BypassAttemptHandler.onEntityPotionEffect(event)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        RespawnManager.onPlayerRespawn(event)
    }

}