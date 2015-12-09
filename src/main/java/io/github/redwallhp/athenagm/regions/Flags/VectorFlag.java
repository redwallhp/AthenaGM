package io.github.redwallhp.athenagm.regions.Flags;

import org.bukkit.util.Vector;

/**
 * Vector region flag
 */
public class VectorFlag extends Flag<Vector> {


    public VectorFlag(String name) {
        super(name);
    }


    public void parseYamlValue(String value) throws InvalidFlagException {
        value = value.replaceAll("\\s+", ""); //strip spaces
        String[] components = value.split(",");
        try {
            double x = Double.parseDouble(components[0]);
            double y = Double.parseDouble(components[1]);
            double z = Double.parseDouble(components[2]);
            setValue(new Vector(x, y, z));
        } catch (NumberFormatException ex) {
            throw new InvalidFlagException(String.format("'%s' is not a valid vector string representation.", value));
        }
    }


    public String toYamlValue() {
        return this.toString();
    }


    public String toString() {
        return String.format("%.2f, %.2f, %.2f", getValue().getX(), getValue().getY(), getValue().getZ());
    }


}
