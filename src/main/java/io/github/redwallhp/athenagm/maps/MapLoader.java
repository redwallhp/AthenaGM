package io.github.redwallhp.athenagm.maps;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.util.UUID;

public class MapLoader {


    private GameMap map;
    private Arena arena;
    private UUID uuid;
    private AthenaGM plugin;


    public MapLoader(GameMap map, Arena arena) {
        this.map = map;
        this.arena = arena;
        this.uuid = UUID.randomUUID();
        this.plugin = arena.getPlugin();
    }


    public GameMap getMap() {
        return this.map;
    }


    public void setMap(GameMap map) {
        this.map = map;
    }


    public UUID getUUID() {
        return this.uuid;
    }


    public void createWorldInstanceCopy(File targetLocation) {
        try {
            recursiveCopyDirectory(this.map.getPath(), targetLocation);
        } catch (Exception ex) {
            plugin.getLogger().warning(String.format("Could not create instanced copy of map '%s'", this.map.getName()));
        }
    }


    public void destroyWorldInstanceCopy(File file) {
        try {
            recursiveDelete(file);
        } catch (Exception ex) {
            plugin.getLogger().warning(String.format("Could not delete world file '%s'", file.getName()));
        }
    }


    public static void cleanUpWorldInstances(File matchesDir) {
        File[] files = matchesDir.listFiles();
        for (File file : files) {
            recursiveDelete(file);
        }
    }


    public static void recursiveDelete(File file) {
        if (file.isDirectory()) {
            for (File next : file.listFiles()) {
                recursiveDelete(next);
            }
        }
        file.delete();
    }


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


    public void load() {
        File instanceLocation = new File(plugin.getMatchesDirectory(), this.uuid.toString());
        createWorldInstanceCopy(instanceLocation);
        World world = new WorldCreator(instanceLocation.getPath()).generator(new VoidGenerator()).createWorld();
        world.setPVP(true);
        world.setSpawnLocation(0, 65, 0);
        world.setSpawnFlags(false, false); //no mobs
        arena.setWorld(world);
        arena.setWorldFile(instanceLocation);
    }


}
