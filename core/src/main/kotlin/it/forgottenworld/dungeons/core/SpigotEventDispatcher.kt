package it.forgottenworld.dungeons.core

import it.forgottenworld.dungeons.core.game.BypassAttemptHandler
import it.forgottenworld.dungeons.core.game.RespawnHandler
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager.finalInstance
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager.combatObjective
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent


class SpigotEventDispatcher : Listener {

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
        event.player.editableDungeon?.onPlayerInteract(event)
        BypassAttemptHandler.onPlayerInteract(event)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        event.entity.uniqueId.combatObjective?.onMobKilled(event.entity.uniqueId)
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.player.finalInstance?.isTpSafe != false) return
        BypassAttemptHandler.onPlayerTeleport(event)
    }

    @EventHandler
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        BypassAttemptHandler.onEntityPotionEffect(event)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        RespawnHandler.onPlayerRespawn(event)
    }

}