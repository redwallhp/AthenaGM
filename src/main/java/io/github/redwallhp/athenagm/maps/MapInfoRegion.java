package io.github.redwallhp.athenagm.maps;


import io.github.redwallhp.athenagm.regions.RegionFlags;
import org.bukkit.util.Vector;


/**
 * Data structure representing a region (as loaded from the map's metadata file).
 */
public class MapInfoRegion {


    private String name;
    private Vector min;
    private Vector max;
    private RegionFlags flags;


    /**
     * Constructor
     * @param name The name of the region
     * @param min  The start point of the cuboid
     * @param max  The end point of the cuboid
     */
    public MapInfoRegion(String name, String min, String max) {
        this.name = name;
        this.min = parseVectorString(min);
        this.max = parseVectorString(max);
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
    public Vector getMin() {
        return min;
    }


    /**
     * The end point of the cuboid
     */
    public Vector getMax() {
        return max;
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
        return (name != null && min != null && max != null);
    }


}
