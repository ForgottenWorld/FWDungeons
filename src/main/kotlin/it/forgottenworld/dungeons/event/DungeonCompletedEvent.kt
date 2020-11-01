package it.forgottenworld.dungeons.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

@Suppress("unused")
class DungeonCompletedEvent(val players: Collection<UUID>, val points: Float) : Event() {

    override fun getHandlers() = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList() = HANDLERS
    }

}