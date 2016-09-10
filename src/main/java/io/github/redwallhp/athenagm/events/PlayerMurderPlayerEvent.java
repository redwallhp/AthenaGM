package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


/**
 * Called when one player kills another player, while in a match
 * @deprecated Use AthenaDeathEvent instead
 */
public class PlayerMurderPlayerEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Match match;
    private Player killer;
    private Player victim;
    private boolean ranged;
    private EntityDamageByEntityEvent entityEvent;
    private long time;
    private int distance;
    private ItemStack item;


    public PlayerMurderPlayerEvent(Match match, Player killer, Player victim, boolean ranged, EntityDamageByEntityEvent entityEvent) {
        this.match = match;
        this.killer = killer;
        this.victim = victim;
        this.ranged = ranged;
        this.entityEvent = entityEvent;
        this.time = System.currentTimeMillis();
        this.distance = (int) Math.round(victim.getLocation().distance(killer.getLocation()));
        this.item = killer.getInventory().getItemInMainHand();
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


    /**
     * Get the time, in milliseconds, that the kill happened
     */
    public long getTime() {
        return time;
    }


    /**
     * Get the distance, in meters/blocks, between the killer and victim
     */
    public int getDistance() {
        return distance;
    }


    /**
     * Get the murder weapon
     */
    public ItemStack getItem() {
        return item;
    }


    /**
     * Get whether this is a ranged or melee attack
     */
    public boolean isRanged() {
        return ranged;
    }


}
