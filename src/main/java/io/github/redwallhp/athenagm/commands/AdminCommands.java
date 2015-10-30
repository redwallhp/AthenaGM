package io.github.redwallhp.athenagm.commands;


import io.github.redwallhp.athenagm.AthenaGM;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommands implements CommandExecutor {


    private AthenaGM plugin;


    public AdminCommands(AthenaGM plugin) {
        this.plugin = plugin;
        plugin.getCommand("athena").setExecutor(this);
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

        return false;

    }


    private void reloadCommand(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /athena reload <what>");
            sender.sendMessage(ChatColor.RED + "What: permissions");
            return;
        }

        String what = args[1];

        if (what.equalsIgnoreCase("permissions")) {
            plugin.getModule("permissions").reload();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Permissions reloaded.");
        }

    }


}
