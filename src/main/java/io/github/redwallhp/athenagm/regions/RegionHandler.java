package io.github.redwallhp.athenagm.regions;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.maps.GameMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegionHandler {


    private AthenaGM plugin;
    private HashMap<String, CuboidRegion> regions;


    public RegionHandler(AthenaGM plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<String, CuboidRegion>();
    }


    public void addRegion(CuboidRegion region) {
        regions.put(region.getName(), region);
    }


    public void removeRegion(CuboidRegion region) {
        regions.remove(region.getName());
    }


    public CuboidRegion getRegion(String name) {
        return regions.get(name);
    }


    public List<CuboidRegion> applicableRegions(World world, Vector vector) {
        List<CuboidRegion> inRegions = new ArrayList<CuboidRegion>();
        for (CuboidRegion region : regions.values()) {
            if (region.contains(world, vector)) inRegions.add(region);
        }
        return inRegions;
    }


    public List<CuboidRegion> applicableRegions(Location location) {
        return applicableRegions(location.getWorld(), location.toVector());
    }


    /**
     * Load configured regions for a map. This is called when a new match starts.
     */
    public void loadRegions(World world, GameMap map) {
    }


    /**
     * Unload regions belonging to a world. Called when a match world is destructed.
     */
    public void unloadRegions(World world) {
        for (CuboidRegion region : regions.values()) {
            if (region.getWorld().getUID() == world.getUID()) {
                removeRegion(region);
            }
        }
    }


}
