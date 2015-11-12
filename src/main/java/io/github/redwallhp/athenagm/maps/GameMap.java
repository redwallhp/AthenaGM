package io.github.redwallhp.athenagm.maps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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


}
