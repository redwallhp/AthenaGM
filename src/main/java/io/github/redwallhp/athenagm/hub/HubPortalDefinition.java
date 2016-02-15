package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.util.Vector;

/**
 * Defines the properties of a Hub portal, such as its region dimensions
 */
public class HubPortalDefinition {


    private Arena arena;
    private Vector start;
    private Vector end;
    private Vector sign;


    public HubPortalDefinition(Arena arena, Vector start, Vector end, Vector sign) {
        this.arena = arena;
        this.start = start;
        this.end = end;
        this.sign = end;
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


    public Vector getSign() {
        return sign;
    }

}
