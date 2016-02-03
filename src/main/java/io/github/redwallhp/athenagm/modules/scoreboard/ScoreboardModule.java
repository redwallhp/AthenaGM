package io.github.redwallhp.athenagm.modules.scoreboard;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.*;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A module to handle scoreboard things.
 * Team colors: Players' teams are mirrored in the scoreboard system to colorize nameplates.
 * Health line: Display a heart and the player's current health under their name.
 * Sidebar score box: Show match time and overall team scores.
 */
public class ScoreboardModule implements Module {


    private AthenaGM plugin;
    private HashMap<Match, Scoreboard> boards;


    public String getModuleName() {
        return "scoreboard";
    }


    public ScoreboardModule(AthenaGM plugin) {
        this.plugin = plugin;
        this.boards = new HashMap<Match, Scoreboard>();
    }


    public void unload() {}


    /**
     * When a new match is created, prepare the scoreboard for this match
     * and ensure old ones are destroyed.
     */
    @EventHandler
    public void onMatchCreate(MatchCreateEvent event) {
        clearUnusedScoreboards();
        registerMatchScoreboardAndTeams(event.getMatch());
        createSidebarTeamScoreObjective(event.getMatch());
        createPlayerHealthObjective(event.getMatch());
    }


    /**
     * When a player changes team, make sure the scoreboard reflects it
     */
    @EventHandler
    public void onPlayerChangedTeam(PlayerChangedTeamEvent event) {
        Scoreboard board = boards.get(event.getTeam().getMatch());
        org.bukkit.scoreboard.Team sbTeam = board.getTeam(event.getTeam().getId());
        sbTeam.addPlayer(event.getPlayer());
    }


    /**
     * When a player enters a match world, affix the corresponding scoreboard
     */
    @EventHandler
    public void onPlayerEnterMatchWorld(PlayerEnterMatchWorldEvent event) {
        event.getPlayer().setScoreboard(boards.get(event.getMatch()));
    }


    /**
     * Update sidebar score box when points are scored
     */
    @EventHandler
    public void onPlayerScorePointEvent(PlayerScorePointEvent event) {
        Objective objective = boards.get(event.getTeam().getMatch()).getObjective("matchscore");
        Score score = objective.getScore(event.getTeam().getChatColor() + event.getTeam().getName());
        score.setScore(score.getScore() + event.getPointsScored());
    }


    @EventHandler
    public void onMatchTimerTickEvent(MatchTimerTickEvent event) {
        updateMatchTimeDisplay(event.getMatchTimer().getMatch(), event.getMatchTimer().timeLeftInSeconds());
    }


    /**
     * Register the scoreboard and set up the internal teams based on
     * the Match's Team objects.
     */
    private void registerMatchScoreboardAndTeams(Match match) {

        // Create scoreboard
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.boards.put(match, board);

        // Create teams for the scoreboard
        for (Team team : match.getTeams().values()) {
            org.bukkit.scoreboard.Team sbTeam = board.registerNewTeam(team.getId());
            sbTeam.setDisplayName(team.getColoredName());
            sbTeam.setPrefix(team.getChatColor() + "");
        }

        // Add online players to the appropriate scoreboard team objects
        // This will colorize their names in nameplates and the tab list
        for (Team team : match.getTeams().values()) {
            org.bukkit.scoreboard.Team sbTeam = board.getTeam(team.getId());
            for (Player player : team.getPlayers()) {
                sbTeam.addPlayer(player);
            }
        }

    }


    /**
     * Remove scoreboards from ended Matches
     */
    private void clearUnusedScoreboards() {
        List<Match> activeMatches = new ArrayList<Match>();
        for (Arena arena : plugin.getArenaHandler().getArenas()) {
            activeMatches.add(arena.getMatch());
        }
        for (Map.Entry<Match, Scoreboard> entry : boards.entrySet()) {
            if (!activeMatches.contains(entry.getKey())) {
                boards.remove(entry.getKey());
            }
        }
    }


    /**
     * Show the player's health under their nameplate
     */
    private void createPlayerHealthObjective(Match match) {

        Scoreboard board = boards.get(match);
        Objective objective = board.registerNewObjective("playerhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.RED + "❤");

        //Force health objective to update health readout
        for (Team team : match.getTeams().values()) {
            for (Player player : team.getPlayers()) {
                player.setHealth(player.getHealth());
            }
        }

    }


    /**
     * Set up the sidebar score box
     */
    private void createSidebarTeamScoreObjective(Match match) {

        Scoreboard board = boards.get(match);
        Objective objective = board.registerNewObjective("matchscore", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(String.format("Match %s(00:00)", ChatColor.AQUA));

        // Create score lines for each team
        for (Team team : match.getTeams().values()) {
            if (!team.isSpectator()) {
                Score score = objective.getScore(team.getChatColor() + team.getName());
                score.setScore(0);
            }
        }

    }


    /**
     * Update the match time display in the sidebar
     */
    public void updateMatchTimeDisplay(Match match, long secondsLeft) {
        long sec = secondsLeft % 60;
        long min = (secondsLeft / 60) % 60;
        String secString = String.format("%02d", sec);
        String minString = String.format("%02d", min);
        Objective objective = boards.get(match).getObjective("matchscore");
        objective.setDisplayName(String.format("Match %s(%s:%s)", ChatColor.AQUA, minString, secString));
    }


}
