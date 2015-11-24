package io.github.redwallhp.athenagm.commands;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.modules.permissions.PermissionsModule;
import io.github.redwallhp.athenagm.modules.spectator.SpectatorModule;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.utilities.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                sender.sendMessage(ChatColor.RED + "Valid subcommands: reload");
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                reloadCommand(sender, args);
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("region")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Valid subcommands: info");
            }
            else if (args[0].equalsIgnoreCase("info")) {
                regionCommand(sender, args);
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


    private void regionCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't run this command.");
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /region <subcommand>");
            sender.sendMessage(ChatColor.RED + "Valid subcommands: info");
            return;
        }

        String subcommand = args[0];
        Player player = (Player) sender;

        if (subcommand.equalsIgnoreCase("info")) {
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

    }


}
