package io.github.redwallhp.athenagm.matches;


import org.bukkit.entity.Player;

/**
 * Data structure to store a player's score values
 */
public class PlayerScore {


    private Player player;
    private Team team;
    private int points = 0;
    private int kills = 0;
    private int deaths = 0;


    public PlayerScore(Player player, Team team) {
        this.player = player;
        this.team = team;
    }


    public Player getPlayer() {
        return player;
    }


    public Team getTeam() {
        return team;
    }


    public int incrementPoints() {
        points++;
        return points;
    }


    public int decrementPoints() {
        points--;
        return points;
    }


    public int getPoints() {
        return points;
    }


    public void setPoints(int points) {
        this.points = points;
    }


    public int incrementKills() {
        kills++;
        return kills;
    }


    public int decrementKills() {
        kills--;
        return kills;
    }


    public int getKills() {
        return kills;
    }


    public void setKills(int kills) {
        this.kills = kills;
    }


    public int incrementDeaths() {
        deaths++;
        return deaths;
    }


    public int decrementDeaths() {
        deaths--;
        return deaths;
    }


    public int getDeaths() {
        return deaths;
    }


    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }


    public int getOverallScore() {
        return points + kills;
    }


}
