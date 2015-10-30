package io.github.redwallhp.athenagm.modules.Permissions;

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


    public void reload() {
        reloadPermissions();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        addAttachment(event.getPlayer());
        applyPermissions(event.getPlayer());
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeAttachment(event.getPlayer());
    }


    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removeAttachment(event.getPlayer());
    }


    public void addAttachment(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        this.attachments.put(player.getUniqueId(), attachment);
    }


    public void removeAttachment(Player player) {
        if (attachments.containsKey(player.getUniqueId())) {
            player.removeAttachment(attachments.get(player.getUniqueId()));
            this.attachments.remove(player.getUniqueId());
        }
    }


    public void removeAllAttachments() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removeAttachment(player);
        }
    }


    private void copyDefaults() {
        if (!groupsFile.exists()) {
            plugin.saveResource("groups.yml", false);
        }
        if (!usersFile.exists()) {
            plugin.saveResource("users.yml", false);
        }
    }


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


    public void reloadPermissions() {
        loadPermissions();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removePermissions(player);
            applyPermissions(player);
        }
    }


    private void removePermissions(Player player) {
        PermissionAttachment attachment = attachments.get(player.getUniqueId());
        for (String perm : attachment.getPermissions().keySet()) {
            attachment.unsetPermission(perm);
        }
    }


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
