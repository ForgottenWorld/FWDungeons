package it.forgottenworld.dungeons.core

import com.google.inject.Inject
import it.forgottenworld.dungeons.core.game.detection.BypassAttemptHandler
import it.forgottenworld.dungeons.core.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager
import it.forgottenworld.dungeons.core.game.respawn.RespawnManager
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

class SpigotEventDispatcher @Inject constructor(
    private val bypassAttemptHandler: BypassAttemptHandler,
    private val combatObjectiveManager: CombatObjectiveManager,
    private val respawnManager: RespawnManager,
    private val dungeonManager: DungeonManager,
    private val unlockableManager: UnlockableManager
) : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        dungeonManager.getPlayerEditableDungeon(event.entity.uniqueId)?.onDestroy(true)
        dungeonManager.getPlayerInstance(event.entity.uniqueId)?.onPlayerDeath(event.entity)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        dungeonManager.getPlayerEditableDungeon(event.player.uniqueId)?.onDestroy(true)
        dungeonManager.getPlayerInstance(event.player.uniqueId)?.onPlayerLeave(event.player)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.from.x == event.to.x &&
            event.from.y == event.to.y &&
            event.from.z == event.to.z
        ) return
        dungeonManager.getPlayerInstance(event.player.uniqueId)?.onPlayerMove(event.player)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        dungeonManager.getPlayerEditableDungeon(event.player.uniqueId)?.onPlayerInteract(event)
        unlockableManager.onPlayerInteract(event)
        bypassAttemptHandler.onPlayerInteract(event)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        combatObjectiveManager
            .getEntityCombatObjective(event.entity.uniqueId)
            ?.onMobKilled(event.entity.uniqueId)
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (dungeonManager.getPlayerInstance(event.player.uniqueId)?.isTpSafe != false) return
        bypassAttemptHandler.onPlayerTeleport(event)
    }

    @EventHandler
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        bypassAttemptHandler.onEntityPotionEffect(event)
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        respawnManager.onPlayerRespawn(event)
    }

}