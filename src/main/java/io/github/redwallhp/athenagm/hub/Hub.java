package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.maps.VoidGenerator;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class Hub {


    private AthenaGM plugin;
    private HubListener listener;
    private HubConfiguration config;
    private WeakReference<World> world;


    public Hub(AthenaGM plugin) {

        this.plugin = plugin;
        this.world = null;
        this.listener = new HubListener(plugin, this);

        try {
            config = new HubConfiguration(new File(plugin.getDataFolder(), "hub.yml"));
        } catch (IOException ex) {
            plugin.getLogger().warning(ex.getMessage());
        }

        Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
            public void run() {
                loadWorld();
            }
        });

    }


    /**
     * Loads the world from disk and prepares it for use
     */
    private void loadWorld() {
        if (config.getWorldName() == null || config.getWorldName().equalsIgnoreCase("")) return;
        File file = new File(Bukkit.getWorldContainer(), config.getWorldName());
        if (file.exists()) {
            try {
                WorldCreator creator = new WorldCreator(config.getWorldName());
                creator.generator(new VoidGenerator());
                creator.environment(World.Environment.NORMAL);
                creator.generateStructures(false);
                World world = creator.createWorld();
                world.setPVP(false);
                world.setSpawnFlags(false, false); //no mobs
                world.setKeepSpawnInMemory(false);
                this.world = new WeakReference<World>(world);
            } catch (Exception ex) {
                plugin.getLogger().warning("Error loading hub world: " + ex.getMessage());
                if (plugin.config.DEBUG) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * Handle player spawning on join. Ensures a player rejoining will always go to the Hub and not a stale Match.
     * Order of priority:
     * 1. If dedicated mode is on, put the player directly in the specified arena if it exists. Fall back to defaul world.
     * 2. If the world specified in hub.yml exists and is loaded, spawn the player there.
     * 3. If all else fails, teleport them to the default world.
     */
    public void spawnPlayer(Player player) {
        playerSetUp(player);
        if (plugin.config.DEDICATED_ARENA != null) {
            // If dedicated mode is on, put the player directly in an arena, as a hub is not desired
            for (Arena arena : plugin.getArenaHandler().getArenas()) {
                if (arena.getId().equalsIgnoreCase(plugin.config.DEDICATED_ARENA)) {
                    player.teleport(arena.getWorld().getSpawnLocation());
                    return;
                }
            }
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        } else if (getWorld() != null) {
            // Spawn the player in the loaded hub world if the world specified in hub.yml exists
            player.teleport(getWorld().getSpawnLocation());
        } else {
            // Spawn the player in the default world if all else fails
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
    }


    /**
     * Reset player attributes and visibility
     */
    public void playerSetUp(Player player) {
        PlayerUtil.resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.showPlayer(player);
            player.showPlayer(p);
        }
    }


    /**
     * Get an instance of the Hub world
     */
    public World getWorld() {
        if (world != null) {
            return world.get();
        } else {
            return null;
        }
    }


    /**
     * Convenience method to check if a player is in the Hub world
     * @param player Player to check
     * @return True if the player is in the Hub world
     */
    public boolean hasPlayer(Player player) {
        return getWorld() != null && player.getWorld().equals(getWorld());
    }


}
