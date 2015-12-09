package io.github.redwallhp.athenagm.regions.Flags;

/**
 * String region flag
 */
public class StringFlag extends Flag<String> {


    public StringFlag(String name) {
        super(name);
    }


    public void parseYamlValue(String value) throws InvalidFlagException {
        setValue(value);
    }


    public String toYamlValue() {
        return getValue();
    }


    public String toString() {
        return getValue();
    }


}
