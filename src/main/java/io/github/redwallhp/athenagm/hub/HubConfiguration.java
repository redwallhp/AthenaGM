package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Represents the hub.yml configuration
 */
public class HubConfiguration {


    private AthenaGM plugin;
    private File file;
    private FileConfiguration yaml;
    private String world;
    private List<HubPortalDefinition> portals;


    /**
     * Load and parse the Hub configuration file
     * @param file File object representing the hub.yml file
     * @throws IOException
     */
    public HubConfiguration(AthenaGM plugin, File file) throws IOException {
        this.plugin = plugin;
        this.file = file;
        if (!file.exists()) throw new IOException("Could not find hub.yml file!");
        this.yaml = YamlConfiguration.loadConfiguration(file);
        loadBasicSettings();
        loadPortals();
    }


    private void loadBasicSettings() {
        this.world = yaml.getString("world");
    }


    private void loadPortals() {
        Set<String> keys = yaml.getConfigurationSection("portals").getKeys(false);
        for (String key : keys) {
            Arena arena = plugin.getArenaHandler().getArenaById(key);
            Vector start = getYamlVectorString(String.format("portals.%s.start", key));
            Vector end = getYamlVectorString(String.format("portals.%s.end", key));
            Vector sign = getYamlVectorString(String.format("portals.%s.sign", key));
            if (arena != null && start != null && end != null) {
                portals.add(new HubPortalDefinition(arena, start, end, sign));
            }
        }
    }


    /**
     * The name of the world to load and use for the Hub
     */
    public String getWorldName() {
        return world;
    }


    /**
     * The List of portals for the Hub
     */
    public List<HubPortalDefinition> getPortals() {
        return portals;
    }


    /**
     * Convert a "0,0,0" (x,y,z) string into a vector object
     */
    private Vector getYamlVectorString(String yamlPath) {
        String vectorString = yaml.getString(yamlPath, null);
        vectorString = vectorString.replaceAll("\\s+",""); //strip spaces
        String[] components = vectorString.split(",");
        try {
            double x = Double.parseDouble(components[0]);
            double y = Double.parseDouble(components[1]);
            double z = Double.parseDouble(components[2]);
            return new Vector(x, y, z);
        } catch(NumberFormatException ex) {
            return null;
        }
    }


}
