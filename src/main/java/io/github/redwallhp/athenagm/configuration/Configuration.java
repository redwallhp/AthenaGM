package io.github.redwallhp.athenagm.configuration;

import io.github.redwallhp.athenagm.AthenaGM;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Handles the main plugin configuration.
 * All loaded configuration keys are exposed via public properties.
 */
public class Configuration {

    private final AthenaGM plugin;
    public boolean DEBUG;
    public String NETWORK_NAME;
    public HashMap<String, ConfiguredArena> ARENAS;

    public Configuration(AthenaGM instance) {
        plugin = instance;
        plugin.saveDefaultConfig();
        this.load();
    }

    /**
     * Save the config out to disk
     */
    public void save() {
        plugin.saveConfig();
    }

    /**
     * Load the configuration from disk and set the simple keys to public properties
     */
    public void load() {
        plugin.reloadConfig();
        DEBUG = plugin.getConfig().getBoolean("debug", false);
        NETWORK_NAME = plugin.getConfig().getString("network_name", "Server");
        getArenas();
    }

    /**
     * Handle the loading of arena configuration blocks, creating ConfiguredArena objects
     */
    private void getArenas() {
        this.ARENAS = new HashMap<String, ConfiguredArena>();
        Set<String> ids = plugin.getConfig().getConfigurationSection("arenas").getKeys(false);
        if (ids.size() > 0) {
            for (String id : ids) {
                String name = plugin.getConfig().getString(String.format("arenas.%s.name", id), "Default Arena");
                String gameMode = plugin.getConfig().getString(String.format("arenas.%s.gamemode", id), "deathmatch");
                Integer maxPlayers = plugin.getConfig().getInt(String.format("arenas.%s.maxplayers", id), 20);
                Integer timeLimit = plugin.getConfig().getInt(String.format("arenas.%s.time_limit", id), 600);
                List<String> mapList = plugin.getConfig().getStringList(String.format("arenas.%s.maps", id));
                ConfiguredArena arena = new ConfiguredArena(id, name, gameMode, maxPlayers, timeLimit, mapList);
                this.ARENAS.put(id, arena);
            }
        }
    }

}
