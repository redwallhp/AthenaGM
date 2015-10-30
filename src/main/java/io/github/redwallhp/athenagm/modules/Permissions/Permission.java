package io.github.redwallhp.athenagm.modules.Permissions;


public class Permission {

    private String node;
    private boolean permit;

    public Permission(String node) {
        if (node.charAt(0) == '^') {
            node = node.substring(1, node.length());
            this.permit = false;
        } else {
            this.permit = true;
        }
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    public boolean isPermit() {
        return permit;
    }
}
