package it.forgottenworld.dungeons.core

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.google.inject.Inject
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.game.detection.BypassAttemptHandler
import it.forgottenworld.dungeons.core.game.objective.CombatObjectiveManager
import it.forgottenworld.dungeons.core.game.respawn.RespawnManager
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import kotlin.math.abs

class BukkitEventListener @Inject constructor(
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
        if (abs(event.from.x - event.to.x) < 0.1 &&
            abs(event.from.y - event.to.y) < 0.1 &&
            abs(event.from.z - event.to.z) < 0.1
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
    fun onEntityRemoveFromWorld(event: EntityRemoveFromWorldEvent) {
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

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player || event.entity !is Player) return
        val attackerInstance = dungeonManager.getPlayerInstance(event.damager.uniqueId) ?: return
        val attackedInstance = dungeonManager.getPlayerInstance(event.entity.uniqueId)
        if (attackedInstance === attackerInstance) {
            event.isCancelled = true
        }
    }
}