package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called directly after a new Match object is instantiated (right after the Map and world are set up)
 */
public class MatchCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Match match;

    public MatchCreateEvent(Match match) {
        this.match = match;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Match getMatch() {
        return match;
    }

}
