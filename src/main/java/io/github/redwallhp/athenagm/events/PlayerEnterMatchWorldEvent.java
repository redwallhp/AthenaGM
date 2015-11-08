package io.github.redwallhp.athenagm.events;


import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.matches.Match;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEnterMatchWorldEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Arena arena;
    private World world;
    private Match match;
    private Player player;

    public PlayerEnterMatchWorldEvent(Arena arena, Player player) {
        this.arena = arena;
        this.world = arena.getWorld();
        this.match = arena.getMatch();
        this.player = player;

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Arena getArena() {
        return arena;
    }

    public World getWorld() {
        return world;
    }

    public Match getMatch() {
        return match;
    }

    public Player getPlayer() {
        return player;
    }


}
