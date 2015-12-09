package io.github.redwallhp.athenagm.regions.Flags;

/**
 * Base region flag class
 */
public abstract class Flag<T> {


    private final String name;
    private T value;


    public Flag(String name) {
        this.name = name;
    }


    /**
     * Returns the name of the flag
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the flag's value.
     * Type is dependant on the inheriting class.
     */
    public T getValue() {
        return value;
    }


    /**
     * Set the flag's value.
     * Type is dependant on the inheriting class.
     */
    public void setValue(T value) {
        this.value = value;
    }


    /**
     * Parse a String pulled from the YAML map config and manipulate it into the
     * right type/format.
     * @param value String value from the YAML
     * @throws InvalidFlagException
     */
    public abstract void parseYamlValue(String value) throws InvalidFlagException;


    /**
     * Put the flag value back into the String format for the YAML file
     */
    public abstract String toYamlValue();


    /**
     * Convert the value into a String for easy display
     */
    public abstract String toString();


}
