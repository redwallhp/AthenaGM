package io.github.redwallhp.athenagm.matches;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.MatchStateChangedEvent;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.maps.MapInfoSpawnPoint;
import io.github.redwallhp.athenagm.maps.MapInfoTeam;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents an ongoing Match's state and data
 */
public class Match {


    private Arena arena;
    private UUID uuid;
    private GameMap map;
    private MatchState state;
    private HashMap<String, Team> teams;
    private MatchTimer timer;


    /**
     * Constructor.
     * @param arena The Arena constructing the Match object, which is responsible for loading a world
     * @param uuid The UUID of the instanced map world
     * @param map The object representing the map state and data
     */
    public Match(Arena arena, UUID uuid, GameMap map) {
        this.arena = arena;
        this.uuid = uuid;
        this.map = map;
        this.state = MatchState.WAITING;
        setUpTeams();
    }


    /**
     * Define teams based on the configuration loaded from the map's metadata file.
     * Always includes a spectator team.
     */
    private void setUpTeams() {
        teams = new HashMap<String, Team>();
        Team spectator = new Team(this, "spectator", "gray", "nokit", 99, true);
        teams.put("spectator", spectator);
        for (MapInfoTeam ct : map.getTeams().values()) {
            Team team = new Team(this, ct.getId(), ct.getColor(), ct.getKit(), ct.getSize(), false);
            teams.put(ct.getId(), team);
        }
    }


    /**
     * Changes the MatchState from WAITING (waiting for teams to be ready) to PLAYING, with
     * a specified time limit for the match, starting the gameplay.
     * @param timeLimit Match time in seconds
     * @see MatchState
     */
    public void start(int timeLimit) {
        if (this.state == MatchState.COUNTDOWN || this.state == MatchState.WAITING) {
            setState(MatchState.PLAYING);
            timer = new MatchTimer(this, timeLimit);
            arena.getPlugin().getLogger().info(String.format("Started match \"%s\" for arena \"%s\"", getUUID(), arena.getName()));
        }
    }


    /**
     * Start with the default time limit from the arena configuration.
     */
    public void start() {
        start(arena.getTimeLimit());
    }


    /**
     * Ends the current Match. Either called by MatchTimer or by
     * a gamemode plugin when an objective is completed.
     */
    public void end() {
        if (this.state == MatchState.PLAYING) {
            setState(MatchState.ENDED);
            printWinner();
            NextMatchCountdown countdown = new NextMatchCountdown(arena);
            getPlugin().getLogger().info(String.format("Ended match \"%s\" for arena \"%s\"", getUUID(), arena.getName()));
            playSound(Sound.ENTITY_ENDERDRAGON_DEATH);
        }
    }


    /**
     * Initiate a countdown until the Match starts, beginning the match at the end.
     */
    public void startCountdown() {
        setState(MatchState.COUNTDOWN);
        MatchStartCountdown countdown = new MatchStartCountdown(this);
    }


    /**
     * Get the Match UUID
     * The UUID identifies both the instanced world save and the match itself
     */
    public UUID getUUID() {
        return this.uuid;
    }


    /**
     * Get the map attached to this Match
     */
    public GameMap getMap() {
        return this.map;
    }


    /**
     * Returns the current MatchState
     * @see MatchState
     */
    public MatchState getState() {
        return this.state;
    }


    /**
     * Directly set the MatchState. start() and end() call this internally.
     * Throws a MatchStateChangedEvent that modules and other plugins can use to
     * detect state changes, such as match start.
     * @see MatchStateChangedEvent
     */
    public void setState(MatchState state) {
        MatchState previousState = this.state;
        this.state = state;
        MatchStateChangedEvent event = new MatchStateChangedEvent(this, previousState);
        Bukkit.getPluginManager().callEvent(event);
    }


    /**
     * Is the match gameplay currently active?
     */
    public boolean isPlaying() {
        return (this.state == MatchState.PLAYING);
    }


    /**
     * Exposes the Team objects associated with the Match
     */
    public HashMap<String, Team> getTeams() {
        return teams;
    }


    /**
     * Convenience method to add a player to one of the Teams associated with the Match
     * @param teamId The String key for the Teams HashMap
     * @param player The Player to add to the Team
     * @return Was the operation successful?
     */
    public boolean addPlayerToTeam(String teamId, Player player) {
        return teams.get(teamId).add(player, false);
    }


    /**
     * Get the total number of players across the Match's Teams
     */
    public int getTotalPlayers() {
        int players = 0;
        for (Team team : this.teams.values()) {
            if (team.isSpectator()) continue;
            players = players + team.getPlayers().size();
        }
        return players;
    }


    /**
     * Get a List of the players across the Match's Teams
     */
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<Player>();
        for (Team t : this.getTeams().values()) {
            for (Player p : t.getPlayers()) {
                players.add(p);
            }
        }
        return players;
    }


    /**
     * Get a HashMap of the players across the Match's Teams
     */
    public HashMap<Player, Team> getPlayerTeamMap() {
        HashMap<Player, Team> map = new HashMap<Player, Team>();
        for (Team t : this.getTeams().values()) {
            for (Player p : t.getPlayers()) {
                map.put(p, t);
            }
        }
        return map;
    }


    /**
     * Determine's the player's spawn point, based on configured coordinates listed
     * in the map's metadata file. Includes spectators.
     * @param player The player respawning, as passed by the PlayerRespawnEvent listener in ArenaListener
     * @see io.github.redwallhp.athenagm.arenas.ArenaListener
     * @return A Location object representing the coordinates and camera yaw for respawn
     */
    public Location getSpawnPoint(Player player) {
        Team team = PlayerUtil.getTeamForPlayer(this, player);
        if (team == null) {
            team = this.teams.get("spectator");
        }
        List<MapInfoSpawnPoint> teamPoints = new ArrayList<MapInfoSpawnPoint>();
        for (MapInfoSpawnPoint point : map.getSpawnPoints()) {
            if (point.getTeam().equals(team.getId())) {
                teamPoints.add(point);
            }
        }
        MapInfoSpawnPoint point = teamPoints.get(new Random().nextInt(teamPoints.size()));
        return new Location(this.arena.getWorld(), point.getX(), point.getY(), point.getZ(), point.getYaw(), 0);
    }


    /**
     * Get the plugin instance
     */
    public AthenaGM getPlugin() {
        return arena.getPlugin();
    }


    /**
     * Signals whether there are enough players for the match to start
     */
    public boolean isReadyToStart() {
        for (Team team : teams.values()) {
            if (team.isSpectator()) continue;
            if (team.getPlayers().size() < 1) return false;
        }
        return true;
    }


    /**
     * Get the MatchTimer for this Match
     */
    public MatchTimer getTimer() {
        return timer;
    }


    /**
     * Broadcast a message to all players in this Match
     */
    public void broadcast(String message) {
        for (Team team : teams.values()) {
            for (Player player : team.getPlayers()) {
                player.sendMessage(message);
            }
        }
    }


    /**
     * Play a sound to everyone in the match
     * @param sound The sound to play
     */
    public void playSound(Sound sound, float pitch) {
        Location loc = new Location(arena.getWorld(), 0, 70, 0);
        arena.getWorld().playSound(loc, sound, 100.0f, pitch);
    }


    /**
     * Output the winning team name to chat
     */
    private void printWinner() {
        Team highest = null;
        boolean isTied = false;
        for (Team t : getTeams().values()) {
            if (highest == null || t.getPoints() > highest.getPoints()) {
                highest = t;
            }
        }
        for (Team t : getTeams().values()) {
            if (highest != null && t != highest && t.getPoints() == highest.getPoints()) {
                isTied = true;
            }
        }
        if (highest != null && highest.getPoints() > 0 && !isTied) {
            broadcast(String.format("%s%s won the match!", highest.getColoredName(), ChatColor.DARK_AQUA));
        }
    }


    /**
     * Play a sound to everyone in the match
     * @param sound The sound to play
     */
    public void playSound(Sound sound) {
        playSound(sound, 1.0f);
    }


    /**
     * Get the World object from the Match's Arena
     * @return The World associated with this Match
     */
    public World getWorld() {
        return arena.getWorld();
    }


}
