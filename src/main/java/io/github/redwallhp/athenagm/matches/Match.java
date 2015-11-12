package io.github.redwallhp.athenagm.matches;


import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.MatchStateChangedEvent;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.maps.MapInfoSpawnPoint;
import io.github.redwallhp.athenagm.maps.MapInfoTeam;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
     * @param timeLimit
     * @see MatchState
     */
    public void start(int timeLimit) {
        if (this.state == MatchState.WAITING) {
            setState(MatchState.PLAYING);
            // Start MatchTimer, which will end the round, or a gamemode plugin will call the end() method
            // when an objective is met.
            // todo: start MatchTimer
        }
    }


    /**
     * Ends the current Match.
     */
    public void end() {
        if (this.state == MatchState.PLAYING) {
            setState(MatchState.ENDED);
        }
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


}
