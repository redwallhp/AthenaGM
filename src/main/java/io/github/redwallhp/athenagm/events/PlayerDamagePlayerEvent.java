package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Called when a player damages another player, if they are in a match
 */
public class PlayerDamagePlayerEvent extends Event implements Cancellable {


    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Match match;
    private Player damager;
    private Player victim;
    private EntityDamageByEntityEvent entityEvent;


    public PlayerDamagePlayerEvent(Match match, Player damager, Player victim, EntityDamageByEntityEvent entityEvent) {
        this.match = match;
        this.damager = damager;
        this.victim = victim;
        this.entityEvent = entityEvent;
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


    /**
     * Is this player trying to attack a team member?
     */
    public boolean isFriendlyFire() {
        return (PlayerUtil.getTeamForPlayer(match, damager) == PlayerUtil.getTeamForPlayer(match, victim));
    }


    /**
     * Get the match the players are in
     */
    public Match getMatch() {
        return match;
    }


    /**
     * Get the attacking player
     */
    public Player getDamager() {
        return damager;
    }


    /**
     * Get the player who was hit
     */
    public Player getVictim() {
        return victim;
    }


    /**
     * Get the original EntityDamageByEntityEvent, so you can inspect damage info
     */
    public EntityDamageByEntityEvent getEntityEvent() {
        return entityEvent;
    }


}
