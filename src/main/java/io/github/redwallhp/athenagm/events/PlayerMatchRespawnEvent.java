package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player spawns in a world owned by a Match. This includes respawning
 * after a death, as well as joining a team. The Match object assigns the spawn location
 * based on the configured points it loaded from the map config.
 * @see Match
 */
public class PlayerMatchRespawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Match match;
    private Location respawnLocation;

    public PlayerMatchRespawnEvent(Player player, Match match, Location respawnLocation) {
        this.player = player;
        this.match = match;
        this.respawnLocation = respawnLocation;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Match getMatch() {
        return match;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }

}
