package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.maps.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

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
     * Get an instance of the Hub world
     */
    public World getWorld() {
        if (world != null) {
            return world.get();
        } else {
            return null;
        }
    }


}
