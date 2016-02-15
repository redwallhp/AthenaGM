package io.github.redwallhp.athenagm.regions;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.maps.MapInfoRegion;
import io.github.redwallhp.athenagm.regions.Flags.Flag;
import io.github.redwallhp.athenagm.regions.listeners.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.*;

public class RegionHandler {


    private AthenaGM plugin;
    private HashMap<UUID, LinkedHashMap<String, CuboidRegion>> regions;


    public RegionHandler(AthenaGM plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<UUID, LinkedHashMap<String, CuboidRegion>>();
        listen();
    }


    private void listen() {
        BlockPlaceListener blockPlaceListener = new BlockPlaceListener(this);
        BlockBreakListener blockBreakListener = new BlockBreakListener(this);
        PlayerMovementListener playerMovementListener = new PlayerMovementListener(this);
        PlayerInteractListener playerInteractListener = new PlayerInteractListener(this);
        EnvironmentalListener environmentalListener = new EnvironmentalListener(this);
    }


    /**
     * Add a region to the map of currently loaded regions
     */
    public void addRegion(CuboidRegion region) {
        UUID worldId = region.getWorld().getUID();
        if (!regions.containsKey(worldId)) regions.put(worldId, new LinkedHashMap<String, CuboidRegion>());
        regions.get(worldId).put(region.getName(), region);
    }


    /**
     * Remove a region from the map of currently loaded regions
     */
    public void removeRegion(CuboidRegion region) {
        UUID worldId = region.getWorld().getUID();
        if (regions.containsKey(worldId)) {
            regions.get(worldId).remove(region.getName());
            if (regions.get(worldId).size() < 1) regions.remove(worldId);
        }
    }


    /**
     * Get a region by its name
     */
    public CuboidRegion getRegion(World world, String name) {
        if (regions.containsKey(world.getUID())) {
            return regions.get(world.getUID()).get(name);
        } else {
            return null;
        }
    }


    /**
     * Returns a list of all regions that intersect a given vector point in a world.
     * @param world The world the vector is in
     * @param vector The point to check for applicable regions
     * @return A list of region objects
     */
    public List<CuboidRegion> getAllApplicableRegions(World world, Vector vector) {
        List<CuboidRegion> inRegions = new ArrayList<CuboidRegion>();
        if (regions.containsKey(world.getUID())) {
            for (CuboidRegion region : regions.get(world.getUID()).values()) {
                if (region.contains(world, vector)) inRegions.add(region);
            }
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
     * Priority is determined by the priority value in the YAML. If a priority is not specified,
     * it will try to fall back to the configured ordinal index.
     * Regions with a positive priority will take priority over all that don't specify one.
     * @param world The world the vector is in
     * @param vector The point to check for an applicable region
     * @return A region object, or null if one does not exist at the vector's location
     */
    public CuboidRegion getApplicableRegion(World world, Vector vector) {

        // Get the highest priority region
        List<CuboidRegion> applicableRegions = getAllApplicableRegions(world, vector);
        TreeMap<Integer, CuboidRegion> regionMap = new TreeMap<Integer, CuboidRegion>();
        int i = 0;
        for (CuboidRegion rg : applicableRegions) {
            if (rg.getPriority() > 0) {
                regionMap.put(rg.getPriority()+applicableRegions.size()+1, rg);
            } else {
                regionMap.put(i, rg);
            }
            i++;
        }
        if (regionMap.size() < 1) return null;
        CuboidRegion r = regionMap.lastEntry().getValue();

        // Create and return a compound region that inherits flags from lower regions
        CuboidRegion compound = new CuboidRegion(r.getName(), r.getWorld(), r.getStart(), r.getEnd());
        for (CuboidRegion rg : regionMap.values()) {
            for (Map.Entry<String, Flag<?>> flag : rg.getFlags().entrySet()) {
                compound.setFlag(flag.getValue());
            }
        }
        return compound;

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
            region.setPriority(mir.getPriority());
            addRegion(region);
        }
    }


    /**
     * Unload regions belonging to a world. Called when a match world is destructed.
     */
    public void unloadRegions(World world) {
        if (regions.containsKey(world.getUID())) {
            for (CuboidRegion region : regions.get(world.getUID()).values()) {
                if (region.getWorld().getUID() == world.getUID()) {
                    removeRegion(region);
                }
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
