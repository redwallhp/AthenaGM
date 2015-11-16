package io.github.redwallhp.athenagm.arenas;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.PlayerEnterMatchWorldEvent;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.events.PlayerScorePointEvent;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.matches.PlayerScore;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;

public class ArenaListener implements Listener {


    private ArenaHandler arenaHandler;
    private AthenaGM plugin;


    public ArenaListener(ArenaHandler arenaHandler) {
        this.arenaHandler = arenaHandler;
        this.plugin = arenaHandler.getPluginInstance();
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {

        // temporary measure to ensure player is kicked back to the hub if they rejoin later
        // Will rework after the minigames hub world system is fleshed out more
        // Hub will need to set player visibility to counter spectator mode stuff
        Player player = event.getPlayer();
        PlayerUtil.resetPlayer(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));

        // Reset player visibility
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.showPlayer(player);
            player.showPlayer(p);
        }

    }


    /**
     * Do player setup and emit PlayerEnterMatchWorldEvent when a player
     * joins a match world. Otherwise, they're moving to a non-match world,
     * so we clean up and remove them from any teams.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Arena arena = arenaHandler.getArenaForPlayer(event.getPlayer());
        if (arena != null) {
            playerEnterMatchWorld(event.getPlayer());
        } else {
            removePlayer(event.getPlayer());
        }
    }


    /**
     * Clean up when a player quits
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }


    /**
     * Clean up if a player is kicked
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }


    /**
     * Handle respawns, overriding location and emitting a custom event
     * @see PlayerMatchRespawnEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaHandler.getArenaForPlayer(player);
        if (arena != null) {
            Location respawnLocation = arena.getMatch().getSpawnPoint(player);
            PlayerMatchRespawnEvent customEvent = new PlayerMatchRespawnEvent(player, arena.getMatch(), respawnLocation);
            Bukkit.getPluginManager().callEvent(customEvent);
            event.setRespawnLocation(customEvent.getRespawnLocation());
        }
    }


    /**
     * Update PlayerScore on kill
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {

        Player victim = null;
        Player killer = null;

        // Melee kills
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.PLAYER)) {
            victim = (Player) event.getEntity();
            killer = (Player) event.getDamager();
        }

        // Projectile kills
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile a = (Projectile) event.getDamager();
            if (a.getShooter() instanceof Player) {
                victim = (Player) event.getEntity();
                killer = (Player) a.getShooter();
            }
        }

        // Do scoring
        if (killer != null && victim != null) {
            Arena arena = arenaHandler.getArenaForPlayer(victim);
            if (arena != null && event.getEntity().isDead()) {
                Team victimTeam = PlayerUtil.getTeamForPlayer(arena.getMatch(), victim);
                Team killerTeam = PlayerUtil.getTeamForPlayer(arena.getMatch(), killer);
                if (victimTeam != null && killerTeam != null) {
                    killerTeam.getPlayerScore(killer).incrementKills();
                }
            }
        }

    }


    /**
     * Update PlayerScore on death
     */
    @EventHandler(priority = EventPriority.LOW)
    public void EntityDeathEvent(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Arena arena = arenaHandler.getArenaForPlayer(player);
            if (arena != null) {
                Team team = PlayerUtil.getTeamForPlayer(arena.getMatch(), player);
                if (team != null) {
                    team.getPlayerScore(player).incrementDeaths();
                }
            }
        }
    }


    /**
     * Update player score object when a PlayerScorePointEvent is emitted.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerScorePointEvent(PlayerScorePointEvent event) {
        PlayerScore score = event.getTeam().getPlayerScore(event.getPlayer());
        score.incrementPointsBy(event.getPointsScored());
    }


    /**
     * Ensure a player is removed from teams when they quit
     */
    public void removePlayer(Player player) {
        Arena arena = arenaHandler.getArenaForPlayer(player);
        if (arena != null) {
            for (Team team : arena.getMatch().getTeams().values()) {
                team.remove(player);
            }
        }
    }


    /**
     * When a player enters a match world, perform setup and emit a custom event.
     * Move player to spectator team to start.
     * Emit PlayerEnterMatchWorldEvent.
     * Print map information.
     * @see PlayerEnterMatchWorldEvent
     */
    private void playerEnterMatchWorld(Player player) {

        Arena arena = arenaHandler.getArenaForPlayer(player);

        // make them a spectator to start
        arena.getMatch().addPlayerToTeam("spectator", player);

        // player has entered the match world
        PlayerEnterMatchWorldEvent e = new PlayerEnterMatchWorldEvent(arena, player);
        Bukkit.getPluginManager().callEvent(e);

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
