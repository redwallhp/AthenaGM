package io.github.redwallhp.athenagm.modules.Permissions;


import java.util.List;

public class Group {

    private String name;
    private List<String> inherits;
    private List<Permission> permissions;

    public Group(String name, List<String> inherits, List<Permission> permissions) {
        this.name = name;
        this.inherits = inherits;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<String> getInherits() {
        return inherits;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
