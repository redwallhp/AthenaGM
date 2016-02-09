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

        try {
            config = new HubConfiguration(new File(plugin.getDataFolder(), "hub.yml"));
            loadWorld();
        } catch (IOException ex) {
            plugin.getLogger().warning(ex.getMessage());
        }

        this.listener = new HubListener(plugin, this);

    }


    /**
     * Loads the world from disk and prepares it for use
     */
    private void loadWorld() {
        if (config.getWorldName() == null || config.getWorldName().equalsIgnoreCase("")) return;
        File file = new File(Bukkit.getWorldContainer(), config.getWorldName());
        if (file.exists()) {
            World world = new WorldCreator(file.getPath()).generator(new VoidGenerator()).createWorld();
            world.setPVP(false);
            world.setSpawnFlags(false, false); //no mobs
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(false);
            this.world = new WeakReference<World>(world);
        }
    }


    /**
     * Get an instance of the Hub world
     */
    public World getWorld() {
        return world.get();
    }


}
