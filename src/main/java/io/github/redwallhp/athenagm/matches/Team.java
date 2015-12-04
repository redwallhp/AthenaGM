package io.github.redwallhp.athenagm.matches;

import io.github.redwallhp.athenagm.events.PlayerChangeTeamEvent;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.maps.MapInfoKitItem;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import io.github.redwallhp.athenagm.utilities.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Represents a team of players in a Match
 */
public class Team {


    private Match match;
    private String id;
    private String name;
    private String color;
    private String kit;
    private Integer size;
    private Boolean spectator;
    private List<Player> players;
    private HashMap<Player, PlayerScore> playerScores;
    private int points;


    /**
     * Constructor
     * @param match The Match this team is a part of
     * @param id String identifier of the team, as defined in the map YAML. e.g. "red"
     * @param color Textual representation of a ChatColor for the team's color
     * @param kit String identifier of the kit a player on this Team should recieve when they respawn
     * @param size Maximum number of players who can join the team
     * @param spectator Is this the spectator Team?
     */
    public Team(Match match, String id, String color, String kit, Integer size, Boolean spectator) {
        this.match = match;
        this.id = id;
        this.name = StringUtil.capitalize(id);
        this.color = color;
        this.kit = kit;
        this.size = size;
        this.spectator = spectator;
        this.players = new ArrayList<Player>();
        this.playerScores = new HashMap<Player, PlayerScore>();
        this.points = 0;
    }


    /**
     * Adds a player to a team, emitting a PlayerChangeTeamEvent that includes
     * the previous team if there was one.
     * Also calls playerSetup() on the player to clear their inventory and attributes,
     * teleport them to an appropriate respawn location, and emit a PlayerMatchRespawnEvent.
     * @see PlayerChangeTeamEvent
     * @see PlayerMatchRespawnEvent
     * @param player The player to add to this team
     * @param force Override team size quotas
     * @return Success
     */
    public boolean add(Player player, boolean force) {
        if (this.players.size() >= this.size && !force) {
            player.sendMessage(String.format("%sCannot join team '%s' because it is full.", ChatColor.RED, this.name));
            return false;
        }
        Team oldTeam = PlayerUtil.getTeamForPlayer(match, player);
        PlayerChangeTeamEvent event = new PlayerChangeTeamEvent(player, this, oldTeam);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled() || force) {
            this.players.add(player);
            if (oldTeam != null) {
                oldTeam.remove(player);
            }
            playerSetup(player);
            player.sendMessage(String.format("%sYou have joined team %s.", ChatColor.DARK_AQUA, getColoredName()));
            return true;
        } else {
            return false;
        }
    }


    /**
     * Removes a player from a team.
     * e.g. on quit or when add() switches a player from another team
     * @param player The player to remove
     * @return Success
     */
    public boolean remove(Player player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            this.playerScores.remove(player);
            return true;
        }
        return false;
    }


    /**
     * Resets the player inventory and attributes, emits PlayerMatchRespawnEvent and teleports
     * the player to an appropriate respawn location for their team.
     */
    private void playerSetup(Player player) {

        PlayerUtil.resetPlayer(player);

        PlayerMatchRespawnEvent event = new PlayerMatchRespawnEvent(player, getMatch(), getMatch().getSpawnPoint(player));
        Bukkit.getPluginManager().callEvent(event);

        player.teleport(event.getRespawnLocation());

    }


    /**
     * The team color as a String representation of a Bukkit ChatColor
     */
    public String getColor() {
        return this.color;
    }


    /**
     * The team color as a ChatColor
     */
    public ChatColor getChatColor() {
        return ChatColor.valueOf(color.toUpperCase());
    }


    /**
     * The team name with the appropriate ChatColor applied
     */
    public String getColoredName() {
        return getChatColor() + this.name;
    }


    /**
     * The match this Team is a part of
     */
    public Match getMatch() {
        return this.match;
    }


    /**
     * The textual ID of the team. e.g. "red"
     */
    public String getId() {
        return this.id;
    }


    /**
     * Human-friendly representation of the team ID, e.g. "Red"
     */
    public String getName() {
        return this.name;
    }


    /**
     * String identifying the kit this team's players should get on respawn
     */
    public String getKit() {
        return this.kit;
    }


    /**
     * Return the MapInfoKitItem objects that a respawning player on this team should receive.
     * MapInfoKitItem exposes a getItem() method to get the ItemStack.
     */
    public List<MapInfoKitItem> getKitItems() {
        return this.getMatch().getMap().getKitItems(getKit());
    }


    /**
     * The maximum number of players for the team
     */
    public Integer getSize() {
        return this.size;
    }


    /**
     * Is this team the spectator team?
     */
    public Boolean isSpectator() {
        return this.spectator;
    }


    /**
     * Get a List of the players on the team
     */
    public List<Player> getPlayers() {
        return this.players;
    }


    /**
     * Get an object representing the player's score values, to
     * increment, decrement or simply read them.
     */
    public PlayerScore getPlayerScore(Player player) {
        if (playerScores.containsKey(player)) {
            return playerScores.get(player);
        } else {
            PlayerScore newScore = new PlayerScore(player, this);
            playerScores.put(player, newScore);
            return newScore;
        }
    }


    /**
     * Get the total kills for the team
     */
    public int getTotalKills() {
        int kills = 0;
        for (PlayerScore ps : playerScores.values()) {
            kills = kills + ps.getKills();
        }
        return kills;
    }


    /**
     * Get the total deaths for the team
     */
    public int getTotalDeaths() {
        int deaths = 0;
        for (PlayerScore ps : playerScores.values()) {
            deaths = deaths + ps.getDeaths();
        }
        return deaths;
    }


    /**
     * Get the team's score
     */
    public int getPoints() {
        return points;
    }


    /**
     * Increment the team's points by one
     */
    public int incrementPoints() {
        points++;
        return points;
    }


    /**
     * Add an arbitrary value to the team's points
     */
    public int incrementPointsBy(int value) {
        points = points + value;
        return points;
    }


    /**
     * Subtract a point from the team's score
     */
    public int decrementPoints() {
        points--;
        return points;
    }


}
