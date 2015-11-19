package io.github.redwallhp.athenagm.maps;


import io.github.redwallhp.athenagm.regions.RegionFlags;
import org.bukkit.util.Vector;


/**
 * Data structure representing a region (as loaded from the map's metadata file).
 */
public class MapInfoRegion {


    private String name;
    private Vector start;
    private Vector end;
    private int priority;
    private RegionFlags flags;


    /**
     * Constructor
     * @param name The name of the region
     * @param start  The start point of the cuboid
     * @param end  The end point of the cuboid
     */
    public MapInfoRegion(String name, String start, String end, int priority) {
        this.name = name;
        this.start = parseVectorString(start);
        this.end = parseVectorString(end);
        this.priority = priority;
        this.flags = new RegionFlags();
    }


    /**
     * Set the RegionFlags object.
     * This is used verbatim in the final CuboidRegion.
     */
    public void setFlags(RegionFlags flags) {
        this.flags = flags;
    }


    /**
     * The RegionFlags object, which represents the keys and values
     * that determine region listener behaviors.
     */
    public RegionFlags getFlags() {
        return flags;
    }


    /**
     * The name of the region
     */
    public String getName() {
        return name;
    }


    /**
     * The start point of the cuboid
     */
    public Vector getStart() {
        return start;
    }


    /**
     * The end point of the cuboid
     */
    public Vector getEnd() {
        return end;
    }


    /**
     * Get the priority of the region (higher overrides lower)
     */
    public int getPriority() {
        return priority;
    }


    /**
     * Convert a "0,0,0" (x,y,z) string into a vector object
     */
    private Vector parseVectorString(String vectorString) {
        vectorString = vectorString.replaceAll("\\s+",""); //strip spaces
        String[] components = vectorString.split(",");
        try {
            double x = Double.parseDouble(components[0]);
            double y = Double.parseDouble(components[1]);
            double z = Double.parseDouble(components[2]);
            return new Vector(x, y, z);
        } catch(NumberFormatException ex) {
            return null;
        }
    }


    /**
     * Does this object have the required fields?
     */
    public boolean isValid() {
        return (name != null && start != null && end != null);
    }


}
