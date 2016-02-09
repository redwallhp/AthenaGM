package io.github.redwallhp.athenagm.maps;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.UUID;

/**
 * Responsible for instancing and loading world saves associated with maps.
 * Called by an Arena after it selects a map from the rotation and loads its metadata
 * into a GameMap object.
 */
public class MapLoader {


    private GameMap map;
    private Arena arena;
    private UUID uuid;
    private AthenaGM plugin;


    /**
     * Constructor
     * @param map The metadata of the map to load
     * @param arena The Arena calling this MapLoader
     */
    public MapLoader(GameMap map, Arena arena) {
        this.map = map;
        this.arena = arena;
        this.uuid = UUID.randomUUID();
        this.plugin = arena.getPlugin();
    }


    /**
     * Get the GameMap object applied to this MapLoader
     */
    public GameMap getMap() {
        return this.map;
    }


    /**
     * Set the GameMap used
     */
    public void setMap(GameMap map) {
        this.map = map;
    }


    /**
     * Get the generated UUID associated with this instanced world copy
     */
    public UUID getUUID() {
        return this.uuid;
    }


    /**
     * Create a new instanced copy of a world save, copying it from the maps directory to the matches directory
     * @param targetLocation
     */
    public void createWorldInstanceCopy(File targetLocation) {
        try {
            recursiveCopyDirectory(this.map.getPath(), targetLocation);
        } catch (Exception ex) {
            plugin.getLogger().warning(String.format("Could not create instanced copy of map '%s'", this.map.getName()));
        }
    }


    /**
     * Delete an instanced world directory
     * @param file Path to the world
     */
    public void destroyWorldInstanceCopy(File file) {
        try {
            recursiveDelete(file);
        } catch (Exception ex) {
            plugin.getLogger().warning(String.format("Could not delete world file '%s'", file.getName()));
        }
    }


    /**
     * Remove all existing instanced world directories.
     * This is run when the server starts, to clear old ones.
     * @param matchesDir The directory the worlds are copied to by MapLoader ("matches" in the server directory)
     */
    public static void cleanUpWorldInstances(File matchesDir) {
        File[] files = matchesDir.listFiles();
        for (File file : files) {
            recursiveDelete(file);
        }
    }


    /**
     * Delete a directory and its contents
     * @param file The directory to delete
     */
    public static void recursiveDelete(File file) {
        if (file.isDirectory()) {
            for (File next : file.listFiles()) {
                recursiveDelete(next);
            }
        }
        file.delete();
    }


    /**
     * Copy a directory and its contents
     * @param source Directory to copy
     * @param destination Location to copy the directory to
     */
    public static void recursiveCopyDirectory(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) destination.mkdir();
            String[] files = source.list();
            for (String file : files) {
                File src = new File(source, file);
                File dest = new File(destination, file);
                recursiveCopyDirectory(src, dest);
            }
        } else {
            FileUtil.copy(source, destination);
        }
    }


    /**
     * Does the actual loading.
     * Creates a new copy of the map in the matches directory and loads it with a WorldCreator,
     * then sets the arena's world property to the new world object.
     */
    public void load() {
        File instanceLocation = new File(plugin.getMatchesDirectory(), this.uuid.toString());
        createWorldInstanceCopy(instanceLocation);
        World world = new WorldCreator(instanceLocation.getPath()).generator(new VoidGenerator()).createWorld();
        world.setPVP(true);
        world.setSpawnLocation(0, 65, 0);
        world.setSpawnFlags(false, false); //no mobs
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        arena.setWorld(world);
        arena.setWorldFile(instanceLocation);
        plugin.getLogger().info(String.format("Loaded map \"%s\" for arena \"%s\"", getMap().getName(), arena.getName()));
    }


}
