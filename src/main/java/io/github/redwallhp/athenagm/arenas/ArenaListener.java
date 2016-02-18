package io.github.redwallhp.athenagm.arenas;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.*;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
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


    /**
     * Do player setup and emit PlayerEnterMatchWorldEvent when a player
     * joins a match world. Otherwise, they're moving to a non-match world,
     * so we clean up and remove them from any teams.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Arena arena = arenaHandler.getArenaForPlayer(event.getPlayer());
        removePlayer(event.getPlayer());
        if (arena != null) {
            playerEnterMatchWorld(event.getPlayer());
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
     * Call PlayerDamagePlayerEvent when a player attacks another player
     * while in an ongoing match.
     * @see PlayerDamagePlayerEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void triggerPlayerDamagePlayerEvent(EntityDamageByEntityEvent event) {

        Player victim = null;
        Player attacker = null;

        // Melee
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.PLAYER)) {
            victim = (Player) event.getEntity();
            attacker = (Player) event.getDamager();
        }

        // Projectile
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile a = (Projectile) event.getDamager();
            if (a.getShooter() instanceof Player) {
                victim = (Player) event.getEntity();
                attacker = (Player) a.getShooter();
            }
        }

        if (attacker == null || victim == null) return;
        Arena arena = arenaHandler.getArenaForPlayer(victim);
        if (arena == null) return;

        PlayerDamagePlayerEvent e = new PlayerDamagePlayerEvent(arena.getMatch(), attacker, victim, event);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled(true);
        }

    }


    /**
     * Call PlayerMurderPlayerEvent when a player kills another player during a match
     * @see PlayerMurderPlayerEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void triggerPlayerMurderEvent(EntityDeathEvent event) {

        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) return;

        Player victim = null;
        Player killer = null;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

        // Melee kills
        if (e.getEntityType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER)) {
            victim = (Player) e.getEntity();
            killer = (Player) e.getDamager();
        }

        // Projectile kills
        if (e.getEntityType().equals(EntityType.PLAYER) && e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile a = (Projectile) e.getDamager();
            if (a.getShooter() instanceof Player) {
                victim = (Player) e.getEntity();
                killer = (Player) a.getShooter();
            }
        }

        if (killer == null || victim == null) return;
        Arena arena = arenaHandler.getArenaForPlayer(victim);
        if (arena == null) return;

        PlayerMurderPlayerEvent murderEvent = new PlayerMurderPlayerEvent(arena.getMatch(), killer, victim, e);
        Bukkit.getPluginManager().callEvent(murderEvent);

    }


    /**
     * Call PlayerMatchDeathEvent when a player dies during a match
     * @see PlayerMatchDeathEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void triggerPlayerMatchDeathEvent(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Arena arena = arenaHandler.getArenaForPlayer(player);
        if (arena == null) return;
        PlayerMatchDeathEvent e = new PlayerMatchDeathEvent(arena.getMatch(), player, event);
        Bukkit.getPluginManager().callEvent(e);
    }


    /**
     * Update PlayerScore on death
     */
    @EventHandler(priority = EventPriority.LOW)
    public void updateScoreOnDeath(PlayerMatchDeathEvent event) {
        if (event.getTeam() != null) {
            event.getTeam().getPlayerScore(event.getPlayer()).incrementDeaths();
        }
    }


    /**
     * Update PlayerScore on kill
     */
    @EventHandler(priority = EventPriority.LOW)
    public void updateScoreOnKill(PlayerMurderPlayerEvent event) {
        if (event.getVictimTeam() != null && event.getKillerTeam() != null && event.getVictimTeam() != event.getKillerTeam()) {
            event.getKillerTeam().getPlayerScore(event.getKiller()).incrementKills();
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
     * Start the match when there are enough players
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangedTeamEvent(PlayerChangedTeamEvent event) {
        Match match = event.getTeam().getMatch();
        if (match.getState() == MatchState.WAITING && match.isReadyToStart()) {
            match.startCountdown();
        }
    }


    /**
     * Ensure a player is removed from teams when they quit
     */
    public void removePlayer(Player player) {
        for (Arena arena : arenaHandler.getArenas()) {
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
