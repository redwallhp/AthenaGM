package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchStateChangedEvent extends Event {

    private HandlerList handlers = new HandlerList();
    private Match match;
    private MatchState previousState;

    public MatchStateChangedEvent(Match match, MatchState previousState) {
        this.match = match;
        this.previousState = previousState;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Match getMatch() {
        return match;
    }

    public MatchState getCurrentState() {
        return match.getState();
    }

    public MatchState getPreviousState() {
        return previousState;
    }
}
