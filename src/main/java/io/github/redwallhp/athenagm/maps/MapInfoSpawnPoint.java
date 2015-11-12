package io.github.redwallhp.athenagm.maps;

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
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param yaw Camera yaw (horizontal rotation) for the player. Pitch (vertical camera tilt) is always horizon level.
     */
    public MapInfoSpawnPoint(String team, Double x, Double y, Double z, Float yaw) {
        this.team = team;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
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
}
