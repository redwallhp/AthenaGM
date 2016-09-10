package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Called when a player dies in a Match.
 * This comprehensive event replaces the deprecated PlayerMurderPlayerEvent
 * and PlayerMatchDeathEvent events.
 */
public class AthenaDeathEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Match match;
    private Player victim;
    private Player killer;
    private PlayerDeathEvent deathEvent;
    private PlayerDamagePlayerEvent damageEvent;
    private long time;


    public AthenaDeathEvent(Match match, Player victim, PlayerDeathEvent deathEvent) {
        this.match = match;
        this.victim = victim;
        this.deathEvent = deathEvent;
        this.time = System.currentTimeMillis();
        this.damageEvent = null;
        this.killer = null;
    }


    public AthenaDeathEvent(Match match, Player victim, Player killer, PlayerDamagePlayerEvent damageEvent, PlayerDeathEvent deathEvent) {
        this(match, victim, deathEvent);
        this.killer = killer;
        this.damageEvent = damageEvent;
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }


    /**
     * Get the match the player died in
     */
    public Match getMatch() {
        return match;
    }


    /**
     * Get the player who died
     */
    public Player getPlayer() {
        return victim;
    }


    /**
     * The player who killed the victim
     */
    public Player getKiller() {
        return killer;
    }


    /**
     * Get the team the victim is a member of
     */
    public Team getPlayerTeam() {
        return PlayerUtil.getTeamForPlayer(match, victim);
    }


    /**
     * Get the killer's team
     */
    public Team getKillerTeam() {
        return PlayerUtil.getTeamForPlayer(match, killer);
    }


    /**
     * Get the time, in milliseconds, that the kill happened
     */
    public long getTime() {
        return time;
    }


    /**
     * Get whether this was a PvP death or not.
     * @return true if the victim was killed by a player
     */
    public boolean isPvP() {
        return getDamageEvent() != null;
    }


    /**
     * Return the list of items dropped on death.
     * The resulting List can be modified to alter the items dropped during the event.
     */
    public List<ItemStack> getDrops() {
        return deathEvent.getDrops();
    }


    /**
     * Get the amount of experience dropped
     */
    public int getDroppedExp() {
        return deathEvent.getDroppedExp();
    }


    /**
     * Set how much experience will be dropped
     */
    public void setDroppedExp(int exp) {
        deathEvent.setDroppedExp(exp);
    }


    /**
     * Get the PlayerDamagePlayerEvent if the victim was killed by a player.
     * The last event damage event tagged within 7.5 seconds will be associated with a death,
     * so damage leading to an environmental kill can be recorded.
     */
    public PlayerDamagePlayerEvent getDamageEvent() {
        return damageEvent;
    }


    /**
     * Get the original PlayerDeathEvent
     */
    public PlayerDeathEvent getDeathEvent() {
        return deathEvent;
    }


}
