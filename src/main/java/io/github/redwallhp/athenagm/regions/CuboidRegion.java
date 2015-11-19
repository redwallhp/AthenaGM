package io.github.redwallhp.athenagm.regions;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


/**
 * Defines a cuboid region for protections, triggers, etc.
 */
public class CuboidRegion {


    private String name;
    private UUID worldID;
    private Vector min;
    private Vector max;
    private RegionFlags flags;


    /**
     * Primary constructor
     * @param name The name of the region
     * @param world The world the region is a part of
     * @param min The min point of the cuboid
     * @param max The max point of the cuboid
     */
    public CuboidRegion(String name, World world, Vector min, Vector max) {
        this.name = name;
        this.worldID = world.getUID();
        this.min = min;
        this.max = max;
        this.flags = new RegionFlags();
    }


    /**
     * Alternate constructor that accepts Location objects instead of Vectors.
     * The region's world is pulled from one of the Location objects.
     * @param name The region name
     * @param min The min point of the cuboid
     * @param max The max point of the cuboid
     */
    public CuboidRegion(String name, Location min, Location max) {
        this(name, min.getWorld(), min.toVector(), max.toVector());
    }


    /**
     * Alternate constructor for a single-block cuboid
     * @param name The name of the region
     * @param world The world the region is a part of
     * @param vector Describes a single-block cuboid region
     */
    public CuboidRegion(String name, World world, Vector vector) {
        this(name, world, vector, vector);
    }


    /**
     * Alternate constructor for a single-block cuboid, using a Location object.
     * The region's world is pulled from the Location.
     * @param name The name of the region
     * @param location A Location describing a single-block cuboid region
     */
    public CuboidRegion(String name, Location location) {
        this(name, location.getWorld(), location.toVector(), location.toVector());
    }


    /**
     * The name of the region
     */
    public String getName() {
        return name;
    }


    /**
     * The World the region is in
     */
    public World getWorld() {
        return Bukkit.getWorld(worldID);
    }


    /**
     * The min point of the region
     */
    public Vector getMin() {
        return min;
    }


    /**
     * The max point of the region
     */
    public Vector getMax() {
        return max;
    }


    /**
     * Checks if a given vector point exists inside the region bounds
     * @param world The world the point to check is in
     * @param vector The point to check
     */
    public boolean contains(World world, Vector vector) {
        boolean isInVector = vector.isInAABB(min, max);
        boolean isWorld = (Bukkit.getWorld(worldID) != null && world.equals(Bukkit.getWorld(worldID)));
        return (isInVector && isWorld);
    }


    /**
     * Checks if a Location object exists inside the region bounds
     * @param location Location to check
     */
    public boolean contains(Location location) {
        return contains(location.getWorld(), location.toVector());
    }


    /**
     * Center point of the region
     */
    public Vector getCenter() {
        return min.getMidpoint(max);
    }


    /**
     * Get the center point of the region as a Block object
     */
    public Block getCenterBlock() {
        Vector center = getCenter();
        return getWorld().getBlockAt((int) center.getX(), (int) center.getY(), (int) center.getZ());
    }


    /**
     * Get a random Location object inside the region
     */
    public Location getRandomLocation() {
        Random r = new Random();
        double x = r.nextInt((int)max.getX()-(int)min.getX()) + min.getX();
        double y = r.nextInt((int)max.getY()-(int)min.getY()) + min.getY();
        double z = r.nextInt((int)max.getZ()-(int)min.getZ()) + min.getZ();
        return new Location(getWorld(), x, y, z);
    }


    /**
     * Returns a List of every Block contained by the region
     */
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = (int)min.getX(); x < max.getX(); x++) {
            for (int y = (int)min.getY(); x < max.getY(); y++) {
                for (int z = (int)min.getZ(); x < max.getZ(); z++) {
                    blocks.add(getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }


    /**
     * Get the region's RegionFlags object
     */
    public RegionFlags getFlags() {
        return flags;
    }


    /**
     * Replace the entire RegionFlags object
     */
    public void setFlags(RegionFlags flags) {
        this.flags = flags;
    }


}
