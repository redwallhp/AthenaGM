package io.github.redwallhp.athenagm.events;


import io.github.redwallhp.athenagm.matches.MatchTimer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called every time a MatchTimer "ticks," which is roughly once per second (20 game ticks).
 * The accessible MatchTimer method can be used to determine the time left in the Match.
 */
public class MatchTimerTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private MatchTimer matchTimer;

    public MatchTimerTickEvent(MatchTimer matchTimer) {
        this.matchTimer = matchTimer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MatchTimer getMatchTimer() {
        return matchTimer;
    }

}
