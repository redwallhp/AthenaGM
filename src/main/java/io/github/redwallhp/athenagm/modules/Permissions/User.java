package io.github.redwallhp.athenagm.modules.permissions;


import java.util.UUID;

/**
 * Data structure defining a user, as configured in users.yml
 */
public class User {

    private UUID uuid;
    private String group;

    /**
     * Constructor
     * @param uuid The UUID of the player
     * @param group The permission group this player is a part of
     */
    public User(UUID uuid, String group) {
        this.uuid = uuid;
        this.group = group;
    }

    /**
     * Get the UUID of the player
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the permission group the player is in
     */
    public String getGroup() {
        return group;
    }

    /**
     * Change the permission group the player is in
     */
    public void setGroup(String group) {
        this.group = group;
    }

}
