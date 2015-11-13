package io.github.redwallhp.athenagm.modules.permissions;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.modules.Module;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.*;


/**
 * A lightweight permission management module that doesn't care about worlds.
 * Most permission plugins are geared toward servers with static worlds and
 * are not designed to have worlds loaded and deleted constantly. This module
 * is a simple permissions solution that is designed entirely for a minigame server.
 */
public class PermissionsModule implements Module {


    private AthenaGM plugin;
    private HashMap<UUID, PermissionAttachment> attachments;
    private File groupsFile;
    private File usersFile;
    private FileConfiguration groupsConfig;
    private FileConfiguration usersConfig;
    private HashMap<String, Group> groups;
    private HashMap<UUID, User> users;


    public String getModuleName() {
        return "permissions";
    }


    public PermissionsModule(AthenaGM plugin) {
        this.plugin = plugin;
        this.attachments = new HashMap<UUID, PermissionAttachment>();
        this.groupsFile = new File(plugin.getDataFolder(), "groups.yml");
        this.usersFile = new File(plugin.getDataFolder(), "users.yml");
        copyDefaults();
        loadPermissions();
    }


    public void unload() {
        removeAllAttachments();
    }


    /**
     * When a player joins, apply a PermissionAttachment to the player so this
     * module can manage permissions for them.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        addAttachment(event.getPlayer());
        applyPermissions(event.getPlayer());
    }


    /**
     * Remove the PermissionAttachment when the player leaves
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeAttachment(event.getPlayer());
    }


    /**
     * Remove the PermissionAttachment when the player leaves
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removeAttachment(event.getPlayer());
    }


    /**
     * Method to add a PermissionAttachment to a player, so the module can manage
     * their permissions. Saves a reference so permissions can be manipulated later.
     * @param player
     */
    public void addAttachment(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        this.attachments.put(player.getUniqueId(), attachment);
    }


    /**
     * Method to remove a PermissionAttachment from a player
     */
    public void removeAttachment(Player player) {
        if (attachments.containsKey(player.getUniqueId())) {
            player.removeAttachment(attachments.get(player.getUniqueId()));
            this.attachments.remove(player.getUniqueId());
        }
    }


    /**
     * Method to remove all PermissionAttachments from players.
     * Called on module unload.
     */
    public void removeAllAttachments() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removeAttachment(player);
        }
    }


    /**
     * Move the default configuration files from the jar to the plugin's data directory.
     * groups.yml sets up groups and the permissions they have.
     * users.yml is for user-group membership.
     */
    private void copyDefaults() {
        if (!groupsFile.exists()) {
            plugin.saveResource("groups.yml", false);
        }
        if (!usersFile.exists()) {
            plugin.saveResource("users.yml", false);
        }
    }


    /**
     * Parse the YAML files and load the permission settings from them.
     */
    private void loadPermissions() {

        groupsConfig = YamlConfiguration.loadConfiguration(groupsFile);
        usersConfig = YamlConfiguration.loadConfiguration(usersFile);

        // Load groups
        this.groups = new HashMap<String, Group>();
        Set<String> groupNames = groupsConfig.getConfigurationSection("groups").getKeys(false);
        for (String name : groupNames) {
            List<String> inherits = groupsConfig.getStringList(String.format("groups.%s.inherits", name));
            List<String> nodes = groupsConfig.getStringList(String.format("groups.%s.permissions", name));
            List<Permission> permissions = new ArrayList<Permission>();
            for (String node : nodes) {
                permissions.add(new Permission(node));
            }
            Group group = new Group(name, inherits, permissions);
            this.groups.put(name, group);
        }

        // Load users
        this.users = new HashMap<UUID, User>();
        Set<String> userKeys = usersConfig.getConfigurationSection("users").getKeys(false);
        for (String key : userKeys) {
            UUID uuid = UUID.fromString(key);
            String group = usersConfig.getString(String.format("users.%s.group", key));
            User user = new User(uuid, group);
            this.users.put(uuid, user);
        }

    }


    /**
     * Reload permissions by re-parsing the files, removing existing permissions from
     * every online player, and re-adding the new ones.
     */
    public void reloadPermissions() {
        loadPermissions();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removePermissions(player);
            applyPermissions(player);
        }
    }


    /**
     * Remove all permissions from the specified player.
     */
    private void removePermissions(Player player) {
        PermissionAttachment attachment = attachments.get(player.getUniqueId());
        for (String perm : attachment.getPermissions().keySet()) {
            attachment.unsetPermission(perm);
        }
    }


    /**
     * Look up the permissions for the specified player and apply them
     */
    private void applyPermissions(Player player) {
        PermissionAttachment attachment = attachments.get(player.getUniqueId());
        User user = null;
        if (users.containsKey(player.getUniqueId())) {
            user = users.get(player.getUniqueId());
        } else {
            user = new User(player.getUniqueId(), "default");
        }
        if (!this.groups.containsKey(user.getGroup())) {
            user.setGroup("default");
        }
        Group group = this.groups.get(user.getGroup());
        List<Permission> permissions = getGroupPermissions(group);
        for (Permission perm : permissions) {
            attachment.setPermission(perm.getNode(), perm.isPermit());
        }
    }


    /**
     * Get the complete list of permission nodes for a group by
     * iterating all of the inherited groups and consolidating the list.
     */
    private List<Permission> getGroupPermissions(Group group) {
        List<Permission> permissions = new ArrayList<Permission>();
        List<Group> groups = new ArrayList<Group>();
        groups.add(group);
        if (!group.getName().equals("default") && this.groups.containsKey("default")) {
            groups.add(this.groups.get("default"));
        }
        for (String name : group.getInherits()) {
            if (this.groups.containsKey(name)) {
                groups.add(this.groups.get(name));
            }
        }
        for (Group g : groups) {
            for (Permission perm : g.getPermissions()) {
                if (!permissions.contains(perm)) {
                    permissions.add(perm);
                }
            }
        }
        return permissions;
    }


}
