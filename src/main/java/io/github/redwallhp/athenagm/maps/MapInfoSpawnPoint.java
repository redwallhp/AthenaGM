package io.github.redwallhp.athenagm.maps;

public class MapInfoSpawnPoint {


    private String team;
    private Double x;
    private Double y;
    private Double z;
    private Float yaw;


    public MapInfoSpawnPoint(String team, Double x, Double y, Double z, Float yaw) {
        this.team = team;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }


    public String getTeam() {
        return team;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public Float getYaw() {
        return yaw;
    }
}
