package io.github.redwallhp.athenagm.commands;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.modules.voting.VotingModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommands implements CommandExecutor {


    private AthenaGM plugin;


    public ArenaCommands(AthenaGM plugin) {
        this.plugin = plugin;
        plugin.getCommand("hub").setExecutor(this);
        plugin.getCommand("arenas").setExecutor(this);
        plugin.getCommand("join").setExecutor(this);
        plugin.getCommand("votemap").setExecutor(this);
        plugin.getCommand("votenext").setExecutor(this);
        plugin.getCommand("vote").setExecutor(this);
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("hub")) {
            if (sender instanceof Player) {
                plugin.getHub().spawnPlayer((Player) sender);
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("arenas")) {
            listArenas(sender);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("join")) {
            if (args.length == 1) {
                joinArena(sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /join <arena id>");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("votemap")) {
            voteMap(sender, args);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("votenext")) {
            voteNext(sender);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("vote")) {
            if (args.length == 1) {
                vote(sender, args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /vote <choice>");
            }
            return true;
        }

        return false;

    }


    private void listArenas(CommandSender sender) {
        for (Arena arena : plugin.getArenaHandler().getArenas()) {
            int players = arena.getMatch().getTotalPlayers();
            StringBuilder head = new StringBuilder("" + ChatColor.STRIKETHROUGH);
            head.append("--- ");
            head.append(ChatColor.GRAY);
            head.append(ChatColor.BOLD);
            head.append(arena.getName());
            head.append(ChatColor.RESET);
            head.append(ChatColor.STRIKETHROUGH);
            head.append(" ---");
            String id = String.format("%sID: %s%s", ChatColor.GRAY, ChatColor.WHITE, arena.getId());
            String map = String.format("%sMap: %s%s", ChatColor.GRAY, ChatColor.WHITE, arena.getMatch().getMap().getName());
            String pl = String.format("%sPlayers: %s%d/%d", ChatColor.GRAY, ChatColor.WHITE, players, arena.getMaxPlayers());
            sender.sendMessage(head.toString());
            sender.sendMessage(id + " " + map + " " + pl);
        }
        sender.sendMessage(ChatColor.DARK_AQUA + String.format("Use %s/join <id>%s to join an arena.", ChatColor.GREEN, ChatColor.DARK_AQUA));
    }


    private void joinArena(CommandSender sender, String id) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't join an arena.");
            return;
        }
        for (Arena arena : plugin.getArenaHandler().getArenas()) {
            if (arena.getId().equals(id)) {
                Player player = (Player) sender;
                Location loc = arena.getMatch().getSpawnPoint(player);
                player.teleport(loc);
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + String.format("Could not find arena '%s'", id));
    }


    private void voteMap(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't vote.");
            return;
        }
        Player player = (Player) sender;
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);
        String map = (args.length == 1) ? args[0] : "";
        VotingModule module = (VotingModule) plugin.getModule("voting");
        if (arena != null) {
            module.createMapVote(arena, player, map);
        }
    }


    private void voteNext(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't vote.");
            return;
        }
        Player player = (Player) sender;
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);
        VotingModule module = (VotingModule) plugin.getModule("voting");
        if (arena != null) {
            module.createNextMapVote(arena, player);
        }
    }


    private void vote(CommandSender sender, String choice) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can't vote.");
            return;
        }
        Player player = (Player) sender;
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);
        VotingModule module = (VotingModule) plugin.getModule("voting");
        if (arena != null) {
            module.vote(arena, player, choice);
        }
    }


}
