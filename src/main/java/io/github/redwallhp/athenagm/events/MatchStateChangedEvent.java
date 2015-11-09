package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the MatchState of a Match changes
 * e.g. when it goes from active gameplay to a win state
 * @see MatchState
 * @see Match
 */
public class MatchStateChangedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Match match;
    private MatchState previousState;

    public MatchStateChangedEvent(Match match, MatchState previousState) {
        this.match = match;
        this.previousState = previousState;
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

    public MatchState getCurrentState() {
        return match.getState();
    }

    public MatchState getPreviousState() {
        return previousState;
    }
}
