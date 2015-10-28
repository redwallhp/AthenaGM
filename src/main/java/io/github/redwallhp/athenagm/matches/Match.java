package io.github.redwallhp.athenagm.matches;


import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.maps.MapInfoSpawnPoint;
import io.github.redwallhp.athenagm.maps.MapInfoTeam;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Match {


    private Arena arena;
    private UUID uuid;
    private GameMap map;
    private MatchState state;
    private HashMap<String, Team> teams;


    public Match(Arena arena, UUID uuid, GameMap map) {
        this.arena = arena;
        this.uuid = uuid;
        this.map = map;
        this.state = MatchState.WAITING;
        setUpTeams();
    }


    public void setUpTeams() {
        teams = new HashMap<String, Team>();
        Team spectator = new Team(this, "spectator", "Spectator", "gray", "nokit", 99, true);
        teams.put("spectator", spectator);
        for (MapInfoTeam ct : map.getTeams().values()) {
            Team team = new Team(this, ct.getId(), ct.getName(), ct.getColor(), ct.getKit(), ct.getSize(), false);
            teams.put(ct.getId(), team);
        }
    }


    public void start(int timeLimit) {
        if (this.state == MatchState.WAITING) {
            setState(MatchState.PLAYING);
            // Start MatchTimer, which will end the round, or a gamemodep plugin will call the end() method
            // when an objective is met.
            // todo: start MatchTimer
        }
    }


    public void end() {
        if (this.state == MatchState.PLAYING) {
            setState(MatchState.ENDED);
        }
    }


    public UUID getUUID() {
        return this.uuid;
    }


    public GameMap getMap() {
        return this.map;
    }


    public MatchState getState() {
        return this.state;
    }


    public void setState(MatchState state) {
        this.state = state;
        // todo: throw a MatchStateChangedEvent
    }


    public boolean isPlaying() {
        return (this.state == MatchState.PLAYING);
    }


    public HashMap<String, Team> getTeams() {
        return teams;
    }


    public boolean addPlayerToTeam(String teamId, Player player) {
        return teams.get(teamId).add(player, false);
    }


    public int getTotalPlayers() {
        int players = 0;
        for (Team team : this.teams.values()) {
            if (team.isSpectator()) continue;
            players = players + team.getPlayers().size();
        }
        return players;
    }


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
