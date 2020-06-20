package it.forgottenworld.dungeons.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DungeonCompletedEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    Set<String> players;
    float points;

    public DungeonCompletedEvent(Set<String> players, float points) {
        this.players = players;
        this.points = points;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
