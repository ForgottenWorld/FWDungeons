package it.forgottenworld.dungeons.event.listener

import it.forgottenworld.dungeons.event.TriggerEvent
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.model.interactiveelement.Trigger
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class TriggerActivationHandler : Listener {

    @EventHandler
    fun onTrigger(event: TriggerEvent) {
        val player = Bukkit.getPlayer(event.playerUuid) ?: return
        if (event.erase) player.collidingTrigger?.onPlayerExit(player)
        if (event.triggerId == -1) return
        val inst = player.finalInstance
            ?: player.editableDungeon?.testInstance
            ?: return
        inst.triggers[event.triggerId]?.onPlayerEnter(player, inst)
    }

    companion object {

        private val triggerCollisions = mutableMapOf<UUID, Trigger>()

        fun clearAllCollisions() = triggerCollisions.clear()

        var Player.collidingTrigger
            get() = triggerCollisions[uniqueId]
            set(value) {
                value?.let { triggerCollisions[uniqueId] = it }
                    ?: triggerCollisions.remove(uniqueId)
            }

        var UUID.collidingTrigger
            get() = triggerCollisions[this]
            set(value) {
                value?.let { triggerCollisions[this] = it }
                    ?: triggerCollisions.remove(this)
            }
    }
}