package io.github.redwallhp.athenagm.events;

import io.github.redwallhp.athenagm.matches.PlayerScore;
import io.github.redwallhp.athenagm.matches.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * Called by external plugins to signal a "capture" or other match point incrementation.
 * AthenaGM will listen and increment scores as necessary.
 */
public class PlayerScorePointEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Team team;
    private int pointsScored;


    public PlayerScorePointEvent(Player player, Team team, int pointsScored) {
        this.player = player;
        this.team = team;
        this.pointsScored = pointsScored;
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


    public Team getTeam() {
        return team;
    }


    public int getPointsScored() {
        return pointsScored;
    }


    public PlayerScore getPlayerScore() {
        return team.getPlayerScore(player);
    }


}
