package io.github.redwallhp.athenagm.commands;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import io.github.redwallhp.athenagm.utilities.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class MatchCommands implements CommandExecutor {


    private AthenaGM plugin;


    public MatchCommands(AthenaGM plugin) {
        this.plugin = plugin;
        plugin.getCommand("teams").setExecutor(this);
        plugin.getCommand("team").setExecutor(this);
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("teams")) {
            listTeams(sender);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("team")) {
            joinTeam(sender, args);
            return true;
        }

        return false;

    }


    private void listTeams(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must join an arena to list teams.");
            return;
        }

        Player player = (Player) sender;
        Arena arena = PlayerUtil.getArenaForPlayer(plugin.getArenaHandler(), player);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "You must join an arena to list teams.");
            return;
        }

        List<String> teamStrings = new ArrayList<String>();
        for (Team team : arena.getMatch().getTeams().values()) {
            teamStrings.add(String.format("%s%s (%d/%d)%s", team.getChatColor(), team.getId(), team.getPlayers().size(), team.getSize(), ChatColor.RESET));
        }

        String list = StringUtil.joinList(", ", teamStrings);
        sender.sendMessage(String.format("%sTeams: %s", ChatColor.DARK_AQUA, list));

    }


    private void joinTeam(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't join a team.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /team <team id>");
            return;
        }

        Player player = (Player) sender;
        Arena arena = PlayerUtil.getArenaForPlayer(plugin.getArenaHandler(), player);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "You must join an arena first.");
            return;
        }

        for (Team team : arena.getMatch().getTeams().values()) {
            if (team.getId().equalsIgnoreCase(args[0])) {
                team.add(player, false);
                return;
            }
        }

        sender.sendMessage(ChatColor.RED + "Invalid team name.");

    }


}
