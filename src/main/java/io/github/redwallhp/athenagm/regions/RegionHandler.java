package io.github.redwallhp.athenagm.regions;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.maps.MapInfoRegion;
import io.github.redwallhp.athenagm.regions.listeners.BlockBreakListener;
import io.github.redwallhp.athenagm.regions.listeners.BlockPlaceListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class RegionHandler {


    private AthenaGM plugin;
    private HashMap<String, CuboidRegion> regions;


    public RegionHandler(AthenaGM plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<String, CuboidRegion>();
        listen();
    }


    private void listen() {
        BlockPlaceListener blockPlaceListener = new BlockPlaceListener(this);
        BlockBreakListener blockBreakListener = new BlockBreakListener(this);
    }


    /**
     * Add a region to the map of currently loaded regions
     */
    public void addRegion(CuboidRegion region) {
        regions.put(region.getName(), region);
    }


    /**
     * Remove a region from the map of currently loaded regions
     */
    public void removeRegion(CuboidRegion region) {
        regions.remove(region.getName());
    }


    /**
     * Get a region by its name
     */
    public CuboidRegion getRegion(String name) {
        return regions.get(name);
    }


    /**
     * Returns a list of all regions that intersect a given vector point in a world.
     * @param world The world the vector is in
     * @param vector The point to check for applicable regions
     * @return A list of region objects
     */
    public List<CuboidRegion> getAllApplicableRegions(World world, Vector vector) {
        List<CuboidRegion> inRegions = new ArrayList<CuboidRegion>();
        for (CuboidRegion region : regions.values()) {
            if (region.contains(world, vector)) inRegions.add(region);
        }
        return inRegions;
    }


    /**
     * Alternate getAllApplicableRegions() method that takes a Location instead of a Vector.
     * Returns a list of all regions that intersect a given vector point in a world.
     * @param location A Location to check for applicable regions.
     * @return A list of region objects
     */
    public List<CuboidRegion> getAllApplicableRegions(Location location) {
        return getAllApplicableRegions(location.getWorld(), location.toVector());
    }


    /**
     * Returns the region with the highest priority that intersects a given vector point.
     * If the regions have equal priority, their ordinal index will be used instead, with
     * later regions superseding previous ones.
     * @param world The world the vector is in
     * @param vector The point to check for an applicable region
     * @return A region object, or null if one does not exist at the vector's location
     */
    public CuboidRegion getApplicableRegion(World world, Vector vector) {
        TreeMap<Integer, CuboidRegion> regionMap = new TreeMap<Integer, CuboidRegion>();
        for (CuboidRegion rg : getAllApplicableRegions(world, vector)) {
            regionMap.put(rg.getPriority(), rg);
        }
        if (regionMap.size() < 1) return null;
        return regionMap.lastEntry().getValue();
    }


    /**
     * Alternate getApplicableRegion method that takes a Location instead of a Vector.
     * Returns the region with the highest priority that intersects a given vector point.
     * If the regions have equal priority, their ordinal index will be used instead, with
     * later regions superseding previous ones.
     * @param location A Location to check for an applicable region.
     * @return A region object, or null if one does not exist at the vector's location
     */
    public CuboidRegion getApplicableRegion(Location location) {
        return getApplicableRegion(location.getWorld(), location.toVector());
    }


    /**
     * Load configured regions for a map. This is called when a new match starts.
     */
    public void loadRegions(World world, GameMap map) {
        for (MapInfoRegion mir : map.getRegions().values()) {
            CuboidRegion region = new CuboidRegion(mir.getName(), world, mir.getStart(), mir.getEnd());
            region.setFlags(mir.getFlags());
            addRegion(region);
        }
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


    /**
     * Get the plugin reference
     */
    public AthenaGM getPlugin() {
        return plugin;
    }


}
