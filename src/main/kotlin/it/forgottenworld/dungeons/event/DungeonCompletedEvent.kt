package it.forgottenworld.dungeons.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class DungeonCompletedEvent(val players: Set<String>, val points: Float): Event() {

    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    fun getHandlerList(): HandlerList {
        return handlerList
    }
}