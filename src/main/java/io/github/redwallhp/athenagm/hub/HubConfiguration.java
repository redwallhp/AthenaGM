package io.github.redwallhp.athenagm.hub;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Represents the hub.yml configuration
 */
public class HubConfiguration {


    private File file;
    private FileConfiguration yaml;
    private String world;


    /**
     * Load and parse the Hub configuration file
     * @param file File object representing the hub.yml file
     * @throws IOException
     */
    public HubConfiguration(File file) throws IOException {
        this.file = file;
        if (!file.exists()) throw new IOException("Could not find hub.yml file!");
        this.yaml = YamlConfiguration.loadConfiguration(file);
        loadBasicSettings();
    }


    private void loadBasicSettings() {
        this.world = yaml.getString("world");
    }


    /**
     * The name of the world to load and use for the Hub
     */
    public String getWorldName() {
        return world;
    }


}
