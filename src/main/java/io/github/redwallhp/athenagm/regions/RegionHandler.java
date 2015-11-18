package io.github.redwallhp.athenagm.regions;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.maps.GameMap;
import org.bukkit.World;

import java.util.HashMap;

public class RegionHandler {


    private AthenaGM plugin;
    private HashMap<String, CuboidRegion> regions;


    public RegionHandler(AthenaGM plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<String, CuboidRegion>();
    }


    public void loadRegion(CuboidRegion region) {
        regions.put(region.getName(), region);
    }


    public void unloadRegion(CuboidRegion region) {
        regions.remove(region.getName());
    }


    public CuboidRegion getRegion(String name) {
        return regions.get(name);
    }


    /**
     * Load configured regions for a map. This is called when a new match starts.
     */
    public void loadRegions(GameMap map) {
    }


    /**
     * Unload regions belonging to a world. Called when a match world is destructed.
     */
    public void unloadRegions(World world) {
        for (CuboidRegion region : regions.values()) {
            if (region.getWorld().getUID() == world.getUID()) {
                unloadRegion(region);
            }
        }
    }


}
