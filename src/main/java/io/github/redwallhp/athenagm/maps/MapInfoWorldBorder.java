package io.github.redwallhp.athenagm.maps;

/**
 * Data structure representing a world border (as loaded from the map's metadata file).
 */
public class MapInfoWorldBorder {

    private double centerX;
    private double centerY;
    private double radius;


    public MapInfoWorldBorder(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }


    public MapInfoWorldBorder(double radius) {
        this.centerX = 0;
        this.centerY = 0;
        this.radius = radius;
    }


    public double getCenterX() {
        return centerX;
    }


    public double getCenterY() {
        return centerY;
    }


    public double getRadius() {
        return radius;
    }


    public boolean isEnabled() {
        return (radius > 0);
    }


}
