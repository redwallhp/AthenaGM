package io.github.redwallhp.athenagm.regions.Flags;

/**
 * Double region flag
 */
public class DoubleFlag extends Flag<Double> {


    public DoubleFlag(String name) {
        super(name);
    }


    public void parseYamlValue(String value) throws InvalidFlagException {
        value = value.trim();
        try {
            setValue(Double.parseDouble(value));
        } catch (NumberFormatException ex) {
            throw new InvalidFlagException(String.format("'%s' is not a valid Double value.", value));
        }
    }


    public String toYamlValue() {
        return getValue().toString();
    }


    public String toString() {
        return getValue().toString();
    }


}
