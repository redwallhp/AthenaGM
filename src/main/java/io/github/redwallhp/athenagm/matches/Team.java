package io.github.redwallhp.athenagm.matches;

import io.github.redwallhp.athenagm.events.PlayerChangeTeamEvent;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class Team {


    private Match match;
    private String id;
    private String name;
    private String color;
    private String kit;
    private Integer size;
    private Boolean spectator;
    private List<Player> players;


    public Team(Match match, String id, String name, String color, String kit, Integer size, Boolean spectator) {
        this.match = match;
        this.id = id;
        this.name = name;
        this.color = color;
        this.kit = kit;
        this.size = size;
        this.spectator = spectator;
        this.players = new ArrayList<Player>();
    }


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
            player.sendMessage(String.format("%sYou have joined team %s.", ChatColor.DARK_AQUA, getColoredName()));
            return true;
        } else {
            return false;
        }
    }


    public boolean remove(Player player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            return true;
        }
        return false;
    }


    public String getColor() {
        return this.color;
    }


    public ChatColor getChatColor() {
        return ChatColor.valueOf(color.toUpperCase());
    }


    public String getColoredName() {
        return getChatColor() + this.name;
    }


    public Match getMatch() {
        return this.match;
    }


    public String getId() {
        return this.id;
    }


    public String getName() {
        return this.name;
    }


    public String getKit() {
        return this.kit;
    }


    public Integer getSize() {
        return this.size;
    }


    public Boolean isSpectator() {
        return this.spectator;
    }


    public List<Player> getPlayers() {
        return this.players;
    }


}
