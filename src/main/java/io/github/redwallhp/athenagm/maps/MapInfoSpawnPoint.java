package io.github.redwallhp.athenagm.maps;

import org.bukkit.util.Vector;

/**
 * Data structure representing a player spawn point (as loaded from the map's metadata file).
 */
public class MapInfoSpawnPoint {


    private String team;
    private Double x;
    private Double y;
    private Double z;
    private Float yaw;


    /**
     * Constructor
     * @param team String ID of the team this spawn belongs to. Compared against the Team object's "id" property.
     * @param point String representation of the x,y,z coordinate.
     * @param yaw Camera yaw (horizontal rotation) for the player. Pitch (vertical camera tilt) is always horizon level.
     */
    public MapInfoSpawnPoint(String team, String point, Float yaw) {
        this.team = team;
        this.yaw = yaw;
        parseVectorString(point);
    }


    /**
     * String ID of the team this spawn belongs to. Compared against the Team object's "id" property.
     */
    public String getTeam() {
        return team;
    }

    /**
     * X coordinate
     */
    public Double getX() {
        return x;
    }

    /**
     * Y coordinate
     */
    public Double getY() {
        return y;
    }

    /**
     * Z coordinate
     */
    public Double getZ() {
        return z;
    }

    /**
     * Camera yaw (horizontal rotation) for the player. Pitch (vertical camera tilt) is always horizon level.
     */
    public Float getYaw() {
        return yaw;
    }

    /**
     * Convert a "0,0,0" (x,y,z) string into a vector object
     */
    private void parseVectorString(String vectorString) {
        vectorString = vectorString.replaceAll("\\s+",""); //strip spaces
        String[] components = vectorString.split(",");
        try {
            this.x = Double.parseDouble(components[0]);
            this.y = Double.parseDouble(components[1]);
            this.z = Double.parseDouble(components[2]);
        } catch(NumberFormatException ex) {
            this.x = null;
            this.y = null;
            this.z = null;
        }
    }

    /**
     * Does this object have the required fields?
     */
    public boolean isValid() {
        return (this.team != null && this.x != null && this.y != null && this.z != null && this.yaw != null);
    }

}
