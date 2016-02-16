package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

/**
 * Defines the properties of a Hub warp/status sign
 */
public class HubSign {


    private Hub hub;
    private Vector vector;
    private Arena arena;


    public HubSign(Hub hub, Vector vector, Arena arena) {
        this.hub = hub;
        this.vector = vector;
        this.arena = arena;
    }


    public void update() {
        if (hub.getWorld() == null) return;
        Block block = vector.toLocation(hub.getWorld()).getBlock();
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String name = getArena().getName();
            int players = getArena().getMatch().getAllPlayers().size();
            String map = getArena().getMatch().getMap().getName();
            sign.setLine(0, String.format("%s%s", ChatColor.BOLD, name));
            sign.setLine(1, String.format("%s%d players", ChatColor.DARK_GRAY, players));
            sign.setLine(2, "");
            sign.setLine(3, map);
            sign.update();
        }
    }


    public Vector getVector() {
        return vector;
    }


    public Arena getArena() {
        return arena;
    }


}
