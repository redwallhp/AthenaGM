package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the hub.yml configuration
 */
public class HubConfiguration {


    private Hub hub;
    private AthenaGM plugin;
    private File file;
    private FileConfiguration yaml;
    private String world;
    private List<HubPortal> portals;
    private List<HubSign> signs;


    /**
     * Load and parse the Hub configuration file
     * @param file File object representing the hub.yml file
     * @throws IOException
     */
    public HubConfiguration(Hub hub, File file) throws IOException {
        this.hub = hub;;
        this.plugin = hub.getPlugin();
        this.file = file;
        this.portals = new ArrayList<HubPortal>();
        this.signs = new ArrayList<HubSign>();
        if (!file.exists()) throw new IOException("Could not find hub.yml file!");
        this.yaml = YamlConfiguration.loadConfiguration(file);
        loadBasicSettings();
        loadPortals();
        loadSigns();
    }


    private void loadBasicSettings() {
        this.world = yaml.getString("world");
    }


    private void loadPortals() {
        if (yaml.getConfigurationSection("portals") == null) return;
        Set<String> keys = yaml.getConfigurationSection("portals").getKeys(false);
        for (String key : keys) {
            Arena arena = plugin.getArenaHandler().getArenaById(key);
            Vector start = getYamlVectorString(String.format("portals.%s.start", key));
            Vector end = getYamlVectorString(String.format("portals.%s.end", key));
            if (arena != null && start != null && end != null) {
                portals.add(new HubPortal(arena, start, end));
            }
        }
    }


    private void loadSigns() {
        List yamlSigns = yaml.getList("signs");
        int i = 0;
        while (i < yamlSigns.size()) {
            Map map = (Map) yamlSigns.get(i);
            Arena arena = plugin.getArenaHandler().getArenaById(map.get("arena").toString());
            Vector vector = parseVectorString(map.get("block").toString());
            if (arena != null && vector != null) {
                signs.add(new HubSign(hub, vector, arena));
            }
            i++;
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
    public List<HubPortal> getPortals() {
        return portals;
    }


    /**
     * The List of signs for the Hub
     */
    public List<HubSign> getSigns() {
        return signs;
    }


    /**
     * Convert a "0,0,0" (x,y,z) string at a given YAML path into a vector object
     */
    private Vector getYamlVectorString(String yamlPath) {
        String vectorString = yaml.getString(yamlPath, null);
        return parseVectorString(vectorString);
    }


    /**
     * Convert a "0,0,0" (x,y,z) string into a vector object
     */
    private Vector parseVectorString(String vectorString) {
        if (vectorString == null) return null;
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
