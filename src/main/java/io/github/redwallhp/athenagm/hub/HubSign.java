package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Defines the properties of a Hub warp/status sign
 */
public class HubSign {


    private Hub hub;
    private Vector vector;
    private Arena arena;
    private String text;


    public HubSign(Hub hub, Vector vector, Arena arena) {
        this.hub = hub;
        this.vector = vector;
        this.arena = arena;
        this.text = null;
    }


    public void update() {
        if (hub.getWorld() == null) return;
        Block block = vector.toLocation(hub.getWorld()).getBlock();
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            if (text != null) {
                applyCustomSignText(sign);
            } else {
                String name = getArena().getName();
                int players = getArena().getMatch().getTotalPlayers();
                String map = getArena().getMatch().getMap().getName();
                sign.setLine(0, String.format("%s%s", ChatColor.BOLD, name));
                sign.setLine(1, String.format("%s%d players", ChatColor.DARK_GRAY, players));
                sign.setLine(2, "");
                sign.setLine(3, map);
            }
            sign.update();
        }
    }


    public void warp(final Player player) {
        // This only works if it doesn't run on the same tick as a PlayerInteractEvent for some reason
        Bukkit.getScheduler().runTask(hub.getPlugin(), new Runnable() {
            public void run() {
                Location loc = arena.getMatch().getSpawnPoint(player);
                player.teleport(loc);
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
            }
        });
    }


    /**
     * Set custom text for this sign.
     * Allows color codes with the & token.
     * Lines will be split on the "|" pipe character.
     * The following substitutions can be used:
     * %name% - The Arena name
     * %id% - The Arena ID
     * %players% - The number of current players
     * %max% - The maximum number of players the Map supports
     * %map% - The current map
     * @param text The String to apply to the sign
     */
    public void setText(String text) {
        this.text = text;
    }


    /**
     * Apply custom lines of text to the HubSign
     */
    private void applyCustomSignText(Sign sign) {
        String text = ChatColor.translateAlternateColorCodes('&', this.text);
        text = text.replaceAll("%name%", arena.getName());
        text = text.replaceAll("%id%", arena.getId());
        text = text.replaceAll("%players%", arena.getPlayerCount().toString());
        text = text.replaceAll("%max%", arena.getMaxPlayers().toString());
        text = text.replaceAll("%map%", arena.getMatch().getMap().getName());
        String[] lines = text.split("\\|");
        for (int i=0; i<4; i++) {
            if (i < lines.length) {
                sign.setLine(i, lines[i]);
            } else {
                sign.setLine(i, "");
            }
        }
    }


    public Vector getVector() {
        return vector;
    }


    public Arena getArena() {
        return arena;
    }


    public String getText() {
        return text;
    }


}
