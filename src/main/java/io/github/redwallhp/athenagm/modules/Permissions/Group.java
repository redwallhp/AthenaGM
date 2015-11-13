package io.github.redwallhp.athenagm.modules.permissions;


import java.util.List;

/**
 * Data structure defining a permission group
 */
public class Group {

    private String name;
    private List<String> inherits;
    private List<Permission> permissions;

    /**
     * Constructor
     * @param name The name of the group
     * @param inherits List of groups that this group inherits permissions from
     * @param permissions List of permission nodes this group has
     */
    public Group(String name, List<String> inherits, List<Permission> permissions) {
        this.name = name;
        this.inherits = inherits;
        this.permissions = permissions;
    }

    /**
     * Get the name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Get the list of groups that this group inherits permissions from
     */
    public List<String> getInherits() {
        return inherits;
    }

    /**
     * Get the list of permission nodes that this group has
     */
    public List<Permission> getPermissions() {
        return permissions;
    }

}
