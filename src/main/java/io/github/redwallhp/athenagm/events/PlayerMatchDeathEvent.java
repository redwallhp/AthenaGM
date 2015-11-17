package io.github.redwallhp.athenagm.events;


import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


/**
 * Called when a player dies, if they are in a match
 */
public class PlayerMatchDeathEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Match match;
    private Player player;
    private EntityDeathEvent entityEvent;


    public PlayerMatchDeathEvent(Match match, Player player, EntityDeathEvent entityEvent) {
        this.match = match;
        this.player = player;
        this.entityEvent = entityEvent;
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
     * Get the player
     */
    public Player getPlayer() {
        return player;
    }


    /**
     * Get the team the player is a member of
     */
    public Team getTeam() {
        return PlayerUtil.getTeamForPlayer(match, player);
    }


    /**
     * Return the list of items dropped on death.
     * The resulting List can be modified to alter the items dropped during the event.
     */
    public List<ItemStack> getDrops() {
        return entityEvent.getDrops();
    }


    /**
     * Get the amount of experience dropped
     */
    public int getDroppedExp() {
        return entityEvent.getDroppedExp();
    }


    /**
     * Set how much experience will be dropped
     */
    public void setDroppedExp(int exp) {
        entityEvent.setDroppedExp(exp);
    }


    /**
     * Get the original EntityDeathEvent
     */
    public EntityDeathEvent getEntityEvent() {
        return entityEvent;
    }


}
