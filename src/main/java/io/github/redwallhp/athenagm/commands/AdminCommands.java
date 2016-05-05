package io.github.redwallhp.athenagm.commands;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.modules.permissions.PermissionsModule;
import io.github.redwallhp.athenagm.modules.spectator.SpectatorModule;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.utilities.WorldEditUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;

public class AdminCommands implements CommandExecutor {


    private AthenaGM plugin;


    public AdminCommands(AthenaGM plugin) {
        this.plugin = plugin;
        plugin.getCommand("athena").setExecutor(this);
        plugin.getCommand("region").setExecutor(this);
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("athena")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Valid subcommands: reload, forcestart, checkperms");
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                reloadCommand(sender, args);
            }
            else if (args[0].equalsIgnoreCase("forcestart")) {
                forceStartCommand(sender, args);
            }
            else if (args[0].equalsIgnoreCase("checkperms")) {
                checkPerms(sender, args);
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("region")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Valid subcommands: info, select");
            }
            else if (args[0].equalsIgnoreCase("info")) {
                regionInfoCommand(sender, args);
            }
            else if (args[0].equalsIgnoreCase("select")) {
                regionSelectCommand(sender, args);
            }
            return true;
        }

        return false;

    }


    private void reloadCommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /athena reload <what>");
            sender.sendMessage(ChatColor.RED + "What: permissions, helpbook");
            return;
        }

        String what = args[1];

        if (what.equalsIgnoreCase("permissions")) {
            PermissionsModule module = (PermissionsModule) plugin.getModule("permissions");
            module.reloadPermissions();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Permissions reloaded.");
        }

        if (what.equalsIgnoreCase("helpbook")) {
            SpectatorModule module = (SpectatorModule) plugin.getModule("spectator");
            module.getHelpBookItem();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Spectator help book reloaded.");
        }

    }


    private void forceStartCommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /athena forcestart <arena>");
            return;
        }

        for (Arena arena : plugin.getArenaHandler().getArenas()) {
            if (arena.getId().equalsIgnoreCase(args[1])) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Forcing match start.");
                arena.getMatch().startCountdown();
                break;
            }
        }

    }


    private void checkPerms(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /athena checkperms <player>");
        }

        Player player = plugin.getServer().getPlayer(args[1]);
        if (player != null) {
            PermissionsModule module = (PermissionsModule) plugin.getModule("permissions");
            sender.sendMessage("Results printed to console.");
            plugin.getServer().getLogger().info(String.format("--- Permission Check for %s ---", player.getName()));
            plugin.getServer().getLogger().info("UUID: " + player.getUniqueId());
            if (module.getUsers().containsKey(player.getUniqueId())) {
                String group = module.getUsers().get(player.getUniqueId()).getGroup();
                plugin.getServer().getLogger().info("Group: " + group);
            }
            for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                plugin.getServer().getLogger().info("* " + info.getPermission());
            }
            plugin.getServer().getLogger().info("--- End Permission Check ---");
        } else {
            sender.sendMessage(ChatColor.RED + "Could not find online player.");
        }

    }


    private void regionInfoCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't run this command.");
            return;
        }

        Player player = (Player) sender;

        Location loc = player.getLocation();
        CuboidRegion rg = plugin.getRegionHandler().getApplicableRegion(loc.getWorld(), loc.toVector());
        if (rg != null) {
            StringBuilder inherited = new StringBuilder();
            List<CuboidRegion> all = plugin.getRegionHandler().getAllApplicableRegions(loc);
            for (CuboidRegion ir : all) {
                inherited.append(ir.getName());
                if (all.indexOf(ir) < all.size() - 1) inherited.append(", ");
            }
            sender.sendMessage(String.format("%sYou are standing in '%s'", ChatColor.DARK_AQUA, rg.getName()));
            sender.sendMessage(String.format("%sInherits from: %s", ChatColor.DARK_AQUA, inherited.toString()));
            sender.sendMessage(String.format("%sStart: %s, End: %s", ChatColor.DARK_AQUA, rg.getStart().toString(), rg.getEnd().toString()));
        }

    }


    private void regionSelectCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't run this command.");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /region select <name>");
            return;
        }

        CuboidRegion rg = plugin.getRegionHandler().getRegion(player.getWorld(), args[1]);
        if (rg == null) {
            sender.sendMessage(String.format("%sCould not find region \"%s\"", ChatColor.RED, args[1]));
            return;
        }

        if (plugin.getWE() == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit does not appear to be installed.");
        } else {
            WorldEditUtil.setPlayerSelection(player, rg.getWorld(), rg.getMin(), rg.getMax());
            sender.sendMessage(String.format("%sSelected region \"%s\"", ChatColor.DARK_AQUA, rg.getName()));
        }

    }


}
