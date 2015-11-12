package io.github.redwallhp.athenagm.matches;

import io.github.redwallhp.athenagm.events.PlayerChangeTeamEvent;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import io.github.redwallhp.athenagm.utilities.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

        //todo: apply kit (possibly in a Kits module using an event?)

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


}
