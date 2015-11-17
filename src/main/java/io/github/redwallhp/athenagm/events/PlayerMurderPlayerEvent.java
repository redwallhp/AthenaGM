package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


/**
 * Called when one player kills another player, while in a match
 */
public class PlayerMurderPlayerEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Match match;
    private Player killer;
    private Player victim;
    private EntityDamageByEntityEvent entityEvent;


    public PlayerMurderPlayerEvent(Match match, Player killer, Player victim, EntityDamageByEntityEvent entityEvent) {
        this.match = match;
        this.killer = killer;
        this.victim = victim;
        this.entityEvent = entityEvent;
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }


    /**
     * The match the killing took place in
     */
    public Match getMatch() {
        return match;
    }


    /**
     * The player who killed the victim
     */
    public Player getKiller() {
        return killer;
    }


    /**
     * Get the killer's team
     */
    public Team getKillerTeam() {
        return PlayerUtil.getTeamForPlayer(match, killer);
    }


    /**
     * The player who was killed
     */
    public Player getVictim() {
        return victim;
    }


    /**
     * Get the victim's team
     */
    public Team getVictimTeam() {
        return PlayerUtil.getTeamForPlayer(match, victim);
    }


    /**
     * Get the original EntityDamageByEntityEvent
     * e.g. to inspect the DamageCause
     */
    public EntityDamageByEntityEvent getEntityEvent() {
        return entityEvent;
    }


}
