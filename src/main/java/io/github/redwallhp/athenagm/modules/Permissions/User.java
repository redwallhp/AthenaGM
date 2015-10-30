package io.github.redwallhp.athenagm.modules.Permissions;


import java.util.List;
import java.util.UUID;

public class User {

    private UUID uuid;
    private String group;

    public User(UUID uuid, String group) {
        this.uuid = uuid;
        this.group = group;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
