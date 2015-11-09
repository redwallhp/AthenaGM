package io.github.redwallhp.athenagm.events;


import io.github.redwallhp.athenagm.matches.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player joins a team. If they are a part of a team already, a previous team
 * will be included. This should be most cases, save for when a player is assigned to
 * Spectator when they first join an Arena.
 */
public class PlayerChangeTeamEvent extends Event implements Cancellable {


    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Team team;
    private Team previousTeam;


    public PlayerChangeTeamEvent(Player player, Team team, Team previousTeam) {
        this.player = player;
        this.team = team;
        if (previousTeam != null) {
            this.previousTeam = previousTeam;
        }
    }


    public PlayerChangeTeamEvent(Player player, Team team) {
        this(player, team, null);
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }


    public boolean isCancelled() {
        return cancelled;
    }


    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


    public Player getPlayer() {
        return this.player;
    }


    public Team getTeam() {
        return this.team;
    }


    public Team getPreviousTeam() {
        return this.previousTeam;
    }


    public boolean hasPreviousTeam() {
        return (this.previousTeam != null);
    }


}
