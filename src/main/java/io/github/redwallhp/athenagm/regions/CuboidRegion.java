package io.github.redwallhp.athenagm.regions;


import io.github.redwallhp.athenagm.regions.Flags.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;


/**
 * Defines a cuboid region for protections, triggers, etc.
 */
public class CuboidRegion {


    private String name;
    private UUID worldID;
    private Vector start;
    private Vector end;
    private int priority;
    private HashMap<String, Flag<?>> flags;


    /**
     * Primary constructor
     * @param name The name of the region
     * @param world The world the region is a part of
     * @param start The start point of the cuboid
     * @param end The end point of the cuboid
     */
    public CuboidRegion(String name, World world, Vector start, Vector end) {
        this.name = name;
        this.worldID = world.getUID();
        this.start = start;
        this.end = end;
        this.priority = 0;
        this.flags = new HashMap<String, Flag<?>>();
    }


    /**
     * Alternate constructor that accepts Location objects instead of Vectors.
     * The region's world is pulled from one of the Location objects.
     * @param name The region name
     * @param start The start point of the cuboid
     * @param end The end point of the cuboid
     */
    public CuboidRegion(String name, Location start, Location end) {
        this(name, start.getWorld(), start.toVector(), end.toVector());
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
     * The start point of the region
     */
    public Vector getStart() {
        return start;
    }


    /**
     * The end point of the region
     */
    public Vector getEnd() {
        return end;
    }


    /**
     * Get a Vector representing the minimum coordinate for the region
     */
    public Vector getMin() {
        return new Vector(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
    }


    /**
     * Get a Vector representing the maximum coordinate for the region
     */
    public Vector getMax() {
        return new Vector(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
    }


    /**
     * Checks if a given vector point exists inside the region bounds
     * @param world The world the point to check is in
     * @param vector The point to check
     */
    public boolean contains(World world, Vector vector) {
        boolean x = vector.getX() >= getMin().getBlockX() && vector.getX() < getMax().getBlockX()+1;
        boolean y = vector.getY() >= getMin().getBlockY() && vector.getY() < getMax().getBlockY()+1;
        boolean z = vector.getZ() >= getMin().getBlockZ() && vector.getZ() < getMax().getBlockZ()+1;
        boolean isWorld = (Bukkit.getWorld(worldID) != null && world.equals(Bukkit.getWorld(worldID)));
        return (isWorld && x && y && z);
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
        return start.getMidpoint(end);
    }


    /**
     * Get the center point of the region as a Block object
     */
    public Block getCenterBlock() {
        Vector center = getCenter();
        return getWorld().getBlockAt(center.getBlockX(), center.getBlockY(), center.getBlockZ());
    }


    /**
     * Get a random Location object inside the region
     */
    public Location getRandomLocation() {
        List<Block> blocks = getBlocks();
        return blocks.get(new Random().nextInt(blocks.size())).getLocation();
    }


    /**
     * Returns a List of every Block contained by the region
     */
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = (int)getMin().getX(); x <= (int)getMax().getX(); x++) {
            for (int y = (int)getMin().getY(); y <= (int)getMax().getY(); y++) {
                for (int z = (int)getMin().getZ(); z <= (int)getMax().getZ(); z++) {
                    blocks.add(getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }


    /**
     * Get the region's flags
     */
    public HashMap<String, Flag<?>> getFlags() {
        return flags;
    }


    /**
     * Get an individual flag by name
     */
    @SuppressWarnings("unchecked")
    public <T extends Flag> T getFlag(String name) {
        if (flags.containsKey(name)) {
            return (T) flags.get(name);
        } else {
            return null;
        }
    }


    /**
     * Get an individual flag value by name
     * @return The value of the flag, in its proper type
     * @param <T> the flag's type
     * @param <V> the flag value's type
     */
    @SuppressWarnings("unchecked")
    public <T extends Flag<V>, V> V getFlagValue(String name) {
        if (flags.containsKey(name)) {
            return (V) flags.get(name).getValue();
        } else {
            return null;
        }
    }


    /**
     * Set a flag
     */
    public <T extends Flag<?>> void setFlag(T flag) {
        flags.put(flag.getName(), flag);
    }


    /**
     * Replace the entire set of flags
     */
    public void setFlags(HashMap<String, Flag<?>> flags) {
        this.flags = flags;
    }


    /**
     * Get the priority of the region (higher overrides lower)
     */
    public int getPriority() {
        return priority;
    }


    /**
     * Set the priority of the region (higher overrides lower)
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


}
