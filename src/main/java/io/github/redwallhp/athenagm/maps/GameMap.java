package io.github.redwallhp.athenagm.maps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents the configuration loaded from a map's YAML metadata file
 */
public class GameMap {


    private File path;
    private String fileName;
    private String name;
    private String author;
    private String version;
    private String objective;
    private String gameMode;
    private HashMap<String, MapInfoTeam> teams;
    private List<MapInfoSpawnPoint> spawnPoints;
    private HashMap<String, List<MapInfoKitItem>> kits;


    /**
     * Loads a map's agm.yml file and parses it
     * @param mapDir File object pointing to the directory of a specific map
     * @throws IOException
     */
    public GameMap(File mapDir) throws IOException {

        this.path = mapDir;
        this.fileName = mapDir.getName();
        File metaFile = new File(this.path, "agm.yml");
        if (!metaFile.exists()) throw new IOException(String.format("No meta file found at path %s", metaFile.getPath()));
        FileConfiguration metaYaml = YamlConfiguration.loadConfiguration(metaFile);

        loadBasicMeta(metaYaml);
        loadTeams(metaYaml);
        loadSpawnPoints(metaYaml);
        loadKits(metaYaml);

    }


    /**
     * Parse the basic metadata
     * @param meta The FileConfiguration reference from the constructor
     */
    private void loadBasicMeta(FileConfiguration meta) {
        this.name = meta.getString("name");
        this.author = meta.getString("author");
        this.version = meta.getString("version");
        this.objective = meta.getString("objective");
        this.gameMode = meta.getString("gamemode");
    }


    /**
     * Parse the team configuration, creating MapInfoTeam objects
     * to hold the data.
     * @param yaml The FileConfiguration reference from the constructor
     * @see MapInfoTeam
     */
    private void loadTeams(FileConfiguration yaml) {
        this.teams = new HashMap<String, MapInfoTeam>();
        Set<String> ids = yaml.getConfigurationSection("teams").getKeys(false);
        if (ids.size() > 0) {
            for (String id : ids) {
                String color = yaml.getString(String.format("teams.%s.color", id), null);
                String kit = yaml.getString(String.format("teams.%s.kit", id), null);
                Integer size = yaml.getInt(String.format("teams.%s.size", id), 10);
                MapInfoTeam team = new MapInfoTeam(id, color, kit, size);
                if (team.isValidTeam()) {
                    teams.put(id, team);
                }
            }
        }
        if (teams.size() < 1) {
            // add some defaults if teams aren't configured
            MapInfoTeam red = new MapInfoTeam("red", "red", "red", 10);
            MapInfoTeam blue = new MapInfoTeam("blue", "blue", "blue", 10);
            teams.put("red", red);
            teams.put("blue", blue);
        }
    }


    /**
     * Parse the configured spawn points, creating MapInfoSpawnPoint objects
     * to hold the data.
     * @param yaml The FileConfiguration reference from the constructor
     */
    private void loadSpawnPoints(FileConfiguration yaml) {
        this.spawnPoints = new ArrayList<MapInfoSpawnPoint>();
        List yamlPoints = yaml.getList("spawn_points");
        int i = 0;
        while (i < yamlPoints.size()) {
            Map map = (Map) yamlPoints.get(i);
            String team = map.get("team").toString();
            String delimitedPoint = map.get("point").toString();
            Float yaw = Float.parseFloat(map.get("yaw").toString());
            String[] coords = delimitedPoint.split(",");
            if (coords.length != 3) continue;
            if (team.length() < 1) continue;
            Double x = Double.parseDouble(coords[0]);
            Double y = Double.parseDouble(coords[1]);
            Double z = Double.parseDouble(coords[2]);
            MapInfoSpawnPoint spawnPoint = new MapInfoSpawnPoint(team, x, y, z, yaw);
            spawnPoints.add(spawnPoint);
            i++;
        }
    }


    /**
     * Load the kits and save the resulting lists of items in a HashMap
     * @param yaml The FileConfiguration reference from the constructor
     */
    private void loadKits(FileConfiguration yaml) {
        this.kits = new HashMap<String, List<MapInfoKitItem>>();
        Set<String> ids = yaml.getConfigurationSection("kits").getKeys(false);
        for (String id : ids) {
            String inherit = yaml.getString(String.format("kits.%s.inherit", id), null);
            List<MapInfoKitItem> itemList = new ArrayList<MapInfoKitItem>();
            itemList.addAll(loadKit(id, yaml));
            if (inherit != null) {
                itemList.addAll(loadKit(inherit, yaml));
            }
            this.kits.put(id, itemList);
        }
    }


    /**
     * Do the heavy lifting, reading the YAML spaghetti for kits
     * @param kitId The name of the kit to load
     * @param yaml The FileConfiguration reference from the constructor
     */
    private List<MapInfoKitItem> loadKit(String kitId, FileConfiguration yaml) {
        List<MapInfoKitItem> items = new ArrayList<MapInfoKitItem>();
        List yamlItems = yaml.getList(String.format("kits.%s.items", kitId));
        int i = 0;
        while (i < yamlItems.size()) {

            Map map = (Map) yamlItems.get(i);

            if (map.get("slot") == null || map.get("quantity") == null || map.get("material") == null) {
                continue; //skip this item if it doesn't have the required fields
            }

            Integer slot = Integer.parseInt(map.get("slot").toString());
            Integer quantity = Integer.parseInt(map.get("quantity").toString());
            String material = map.get("material").toString();

            MapInfoKitItem item = new MapInfoKitItem(kitId, slot.intValue(), material, quantity.intValue());

            // Colorize the armor if this is a leather armor item
            if (item.getItem().getItemMeta() instanceof LeatherArmorMeta) {
                if (map.get("color") != null) {
                    item.colorizeArmor(map.get("color").toString());
                }
            }

            // Add enchantments
            List yamlEnchants = (List) map.get("enchantments");
            if (yamlEnchants != null && yamlEnchants.size() > 0) {
                int k = 0;
                while (k < yamlEnchants.size()) {
                    Map emap = (Map) yamlEnchants.get(k);
                    if (emap.get("name") == null || emap.get("level") == null) {
                        continue;
                    }
                    String enchant = emap.get("name").toString();
                    Integer level = Integer.parseInt(emap.get("level").toString());
                    item.addEnchantment(enchant, level);
                    k++;
                }
            }

            //Get the ItemStack and continue
            items.add(item);
            i++;

        }
        return items;
    }


    /**
     * Get the File object of the the specific map's directory
     */
    public File getPath() {
        return this.path;
    }

    /**
     * Get the string representation of the path to the specific map's directory
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * The map's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * The map's author
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * The map's version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * The map's objective. This is the description that is printed under the
     * name and author when a player joins. This could be instructions and/or
     * flavor text.
     */
    public String getObjective() {
        return this.objective;
    }

    /**
     * The gamemode this map is intended for. (e.g. "koth")
     * Plugins that handle gameplay for different modes will listen for this identifier.
     */
    public String getGameMode() {
        return this.gameMode;
    }

    /**
     * Get the configured teams. The Match object will use this to build new Team objects
     * @see io.github.redwallhp.athenagm.matches.Match
     * @see io.github.redwallhp.athenagm.matches.Team
     */
    public HashMap<String, MapInfoTeam> getTeams() {
        return this.teams;
    }

    /**
     * Get the configured spawn points. The Match object uses this List in its
     * getSpawnPoint() method, which controls where players respawn, based on team.
     */
    public List<MapInfoSpawnPoint> getSpawnPoints() {
        return this.spawnPoints;
    }

    /**
     * Get a kit by its name
     * @param id String identifier of the kit to return
     */
    public List<MapInfoKitItem> getKitItems(String id) {
        if (this.kits.containsKey(id)) {
            return this.kits.get(id);
        }
        return null;
    }

    /**
     * Get all kits
     */
    public HashMap<String, List<MapInfoKitItem>> getKitsMap() {
        return kits;
    }

}
