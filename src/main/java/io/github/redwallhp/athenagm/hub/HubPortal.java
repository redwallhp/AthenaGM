package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.util.Vector;

/**
 * Defines the properties of a Hub portal, such as its region dimensions
 */
public class HubPortal {


    private Arena arena;
    private Vector start;
    private Vector end;


    public HubPortal(Arena arena, Vector start, Vector end) {
        this.arena = arena;
        this.start = start;
        this.end = end;
    }


    public Arena getArena() {
        return arena;
    }


    public Vector getStart() {
        return start;
    }


    public Vector getEnd() {
        return end;
    }

}
