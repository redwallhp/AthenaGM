package io.github.redwallhp.athenagm.modules.permissions;


/**
 * Data structure defining a permission a group or player may have
 */
public class Permission {

    private String node;
    private boolean permit;

    /**
     * Constructor
     * @param node The permission node. May contain "^" as the first character to act as a negation
     */
    public Permission(String node) {
        if (node.charAt(0) == '^') {
            node = node.substring(1, node.length());
            this.permit = false;
        } else {
            this.permit = true;
        }
        this.node = node;
    }

    /**
     * Get the permission node, with the optional caret removed
     */
    public String getNode() {
        return node;
    }

    /**
     * Is this Permission object permitting the player to access this node...or negating?
     * A negation is used to remove a permission from a group or player, when it would otherwise
     * inherit it from another group.
     * @return true if no caret was passed in the constructor, false if one was
     */
    public boolean isPermit() {
        return permit;
    }

}
