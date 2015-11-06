package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchCreateEvent extends Event {

    private HandlerList handlers = new HandlerList();
    private Match match;

    public MatchCreateEvent(Match match) {
        this.match = match;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Match getMatch() {
        return match;
    }

}
