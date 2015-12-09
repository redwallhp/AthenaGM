package io.github.redwallhp.athenagm.regions.Flags;

/**
 * Integer region flag
 */
public class IntegerFlag extends Flag<Integer> {


    public IntegerFlag(String name) {
        super(name);
    }


    public void parseYamlValue(String value) throws InvalidFlagException {
        value = value.trim();
        try {
            setValue(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            throw new InvalidFlagException(String.format("'%s' is not a valid Integer value.", value));
        }
    }


    public String toYamlValue() {
        return getValue().toString();
    }


    public String toString() {
        return getValue().toString();
    }


}
