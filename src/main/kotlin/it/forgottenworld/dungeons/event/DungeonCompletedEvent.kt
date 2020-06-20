package it.forgottenworld.dungeons.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

class DungeonCompletedEvent(val players: Set<UUID>, val points: Float) : Event() {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

}