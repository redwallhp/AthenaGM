package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerMatchRespawnEvent extends Event {

    private HandlerList handlers = new HandlerList();
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
