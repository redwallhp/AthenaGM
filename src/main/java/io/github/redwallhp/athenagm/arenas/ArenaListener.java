package io.github.redwallhp.athenagm.arenas;


import io.github.redwallhp.athenagm.events.PlayerEnterMatchWorldEvent;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaListener implements Listener {


    private ArenaHandler arenaHandler;


    public ArenaListener(ArenaHandler arenaHandler) {
        this.arenaHandler = arenaHandler;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // temporary measure to ensure player is kicked back to the hub if they rejoin later
        // Will rework after the minigames hub world system is fleshed out more
        Player player = event.getPlayer();
        PlayerUtil.resetPlayer(player);
        player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
        player.setGameMode(GameMode.SURVIVAL);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaHandler.getArenaForPlayer(player);
        if (arena != null) {

            // player has entered the match world
            PlayerEnterMatchWorldEvent e = new PlayerEnterMatchWorldEvent(arena, player);
            Bukkit.getPluginManager().callEvent(e);
            PlayerUtil.resetPlayer(player);

            // make them a spectator to start
            arena.getMatch().addPlayerToTeam("spectator", player);
            player.setGameMode(GameMode.SPECTATOR);

            // print map information
            GameMap map = arena.getMatch().getMap();

            StringBuilder sb = new StringBuilder("" + ChatColor.STRIKETHROUGH);
            sb.append("--- ");
            sb.append(ChatColor.DARK_AQUA);
            sb.append(ChatColor.BOLD);
            sb.append(map.getName());
            sb.append(ChatColor.RESET);
            sb.append(ChatColor.STRIKETHROUGH);
            sb.append(" ---");
            player.sendMessage(sb.toString());

            player.sendMessage(String.format("%sMap Author: %s%s", ChatColor.GRAY, ChatColor.WHITE, map.getAuthor()));
            player.sendMessage(String.format("%sVersion: %s%s", ChatColor.GRAY, ChatColor.WHITE, map.getVersion()));
            player.sendMessage(String.format("%sGame Mode: %s%s", ChatColor.GRAY, ChatColor.WHITE, map.getGameMode()));

            String[] objective = map.getObjective().split(" ");
            StringBuilder line = new StringBuilder("");
            for (String word : objective) {
                line.append(word);
                line.append(" ");
                if (line.length() > 30) {
                    player.sendMessage(line.toString());
                    line.delete(0, line.length());
                }
            }
            if (line.length() > 0 && !line.toString().equals(" ")) {
                player.sendMessage(line.toString());
            }

            player.sendMessage(ChatColor.STRIKETHROUGH + "----------------------");

        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }


    public void removePlayer(Player player) {
        Arena arena = arenaHandler.getArenaForPlayer(player);
        if (arena != null) {
            for (Team team : arena.getMatch().getTeams().values()) {
                team.remove(player);
            }
        }
    }


}
