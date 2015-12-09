package io.github.redwallhp.athenagm.regions.Flags;

/**
 * Boolean region flag
 */
public class BooleanFlag extends Flag<Boolean> {


    public BooleanFlag(String name) {
        super(name);
    }


    public void parseYamlValue(String value) throws InvalidFlagException {
        value = value.trim();
        if (value.equalsIgnoreCase("true")) {
            setValue(true);
        } else if (value.equalsIgnoreCase("false")) {
            setValue(false);
        } else {
            throw new InvalidFlagException(String.format("'%s' is not a valid boolean value.", value));
        }
    }


    public String toYamlValue() {
        return getValue().toString();
    }


    public String toString() {
        return getValue().toString();
    }


}
