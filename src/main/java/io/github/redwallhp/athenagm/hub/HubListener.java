package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HubListener implements Listener {


    AthenaGM plugin;
    Hub hub;


    public HubListener(AthenaGM plugin, Hub hub) {
        this.plugin = plugin;
        this.hub = hub;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerSetUp(event.getPlayer());
        spawnPlayer(event.getPlayer());
    }


    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (hub.getWorld() != null && event.getPlayer().getWorld().equals(hub.getWorld())) {
            playerSetUp(event.getPlayer());
        }
    }


    /**
     * Reset player attributes and visibility
     */
    private void playerSetUp(Player player) {
        PlayerUtil.resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.showPlayer(player);
            player.showPlayer(p);
        }
    }


    /**
     * Handle player spawning on join. Ensures a player rejoining will always go to the Hub and not a stale Match.
     * Order of priority:
     * 1. If dedicated mode is on, put the player directly in the specified arena if it exists. Fall back to defaul world.
     * 2. If the world specified in hub.yml exists and is loaded, spawn the player there.
     * 3. If all else fails, teleport them to the default world.
     */
    private void spawnPlayer(Player player) {
        if (plugin.config.DEDICATED_ARENA != null) {
            // If dedicated mode is on, put the player directly in an arena, as a hub is not desired
            for (Arena arena : plugin.getArenaHandler().getArenas()) {
                if (arena.getId().equalsIgnoreCase(plugin.config.DEDICATED_ARENA)) {
                    player.teleport(arena.getWorld().getSpawnLocation());
                    return;
                }
            }
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        } else if (hub.getWorld() != null) {
            // Spawn the player in the loaded hub world if the world specified in hub.yml exists
            player.teleport(hub.getWorld().getSpawnLocation());
        } else {
            // Spawn the player in the default world if all else fails
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
    }


}
