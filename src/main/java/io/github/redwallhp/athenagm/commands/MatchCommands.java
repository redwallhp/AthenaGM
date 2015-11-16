package io.github.redwallhp.athenagm.commands;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.matches.PlayerScore;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import io.github.redwallhp.athenagm.utilities.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;


public class MatchCommands implements CommandExecutor {


    private AthenaGM plugin;


    public MatchCommands(AthenaGM plugin) {
        this.plugin = plugin;
        plugin.getCommand("teams").setExecutor(this);
        plugin.getCommand("team").setExecutor(this);
        plugin.getCommand("autojoin").setExecutor(this);
        plugin.getCommand("spectate").setExecutor(this);
        plugin.getCommand("score").setExecutor(this);
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

        if (cmd.getName().equalsIgnoreCase("autojoin")) {
            autoJoinTeam(sender);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("spectate")) {
            String[] arguments = { "spectator" };
            joinTeam(sender, arguments);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("score")) {
            printPlayerScore(sender);
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
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);

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
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);

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

        sender.sendMessage(ChatColor.RED + "Invalid team id.");

    }


    private void autoJoinTeam(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't join a team.");
            return;
        }

        Player player = (Player) sender;
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "You must join an arena first.");
            return;
        }

        TreeMap<Integer, Team> teamSizeMap = new TreeMap<Integer, Team>();
        for (Team team : arena.getMatch().getTeams().values()) {
            if (team.getPlayers().size() < team.getSize() || team.getPlayers().size() == 0) {
                teamSizeMap.put(team.getPlayers().size(), team);
            }
        }
        for (Team team : teamSizeMap.values()) {
            if (! team.isSpectator()) team.add(player, false);
        }

    }


    private void printPlayerScore(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't have a score.");
            return;
        }

        Player player = (Player) sender;
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "You must join an arena first.");
            return;
        }
        Team team = PlayerUtil.getTeamForPlayer(arena.getMatch(), player);
        if (team == null || team.isSpectator()) {
            sender.sendMessage(ChatColor.RED + "You must join a team to have a score.");
            return;
        }
        PlayerScore playerScore = team.getPlayerScore(player);

        LinkedHashMap<String, Integer> values = new LinkedHashMap<String, Integer>();
        values.put("Points", playerScore.getPoints());
        values.put("Kills", playerScore.getKills());
        values.put("Deaths", playerScore.getDeaths());
        try {
            values.put("KDR", (playerScore.getKills() / playerScore.getDeaths()));
        } catch(ArithmeticException ex) {
            values.put("KDR", 0);
        }

        StringBuilder sb = new StringBuilder(ChatColor.DARK_AQUA + "Personal score: ");
        for (Map.Entry<String, Integer> pair : values.entrySet()) {
            sb.append(String.format("%s%s: %s%d", ChatColor.AQUA, pair.getKey(), ChatColor.GREEN, pair.getValue()));
            sb.append(" ");
        }
        sender.sendMessage(sb.toString());

    }


}
