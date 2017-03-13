package io.github.redwallhp.athenagm.modules.voting;

import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.maps.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class Vote {


    private Arena arena;
    private VoteType voteType;
    private Map<String, Integer> options;
    private Set<UUID> voted;
    private GameMap map;
    private long timeCreated;


    /**
     * Shared parent constructor
     */
    private Vote(Arena arena) {
        this.arena = arena;
        this.timeCreated = System.currentTimeMillis();
        this.options = new HashMap<String, Integer>();
        this.voted = new HashSet<UUID>();
        this.map = null;
    }


    /**
     * Construct a Vote for an immediate map change, with a specified map
     * @param map the map to change to if the vote passes
     */
    public Vote(Arena arena, GameMap map) {
        this(arena);
        this.voteType = VoteType.CHANGE_MAP;
        this.map = map;
        options.put("Yes", 0);
        options.put("No", 0);
    }


    /**
     * Construct Vote with list of map options
     * @param options list of Strings
     */
    public Vote(Arena arena, List<String> options) {
        this(arena);
        this.voteType = VoteType.NEXT_MAP;
        for (String option : options) {
            this.options.put(option, 0);
        }
    }


    /**
     * Handle a player's vote
     * @param player the player voting
     * @param choice the option the player selected
     * @return true if successful
     */
    public boolean cast(Player player, String choice) {
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(choice)) {
                entry.setValue(entry.getValue() + 1);
                setVoted(player.getUniqueId());
                return true;
            }
        }
        return false;
    }


    /**
     * Check vote conditions. This is invoked once per second by VotingModule
     * @return true if the vote passes and should be removed from circulation
     */
    public boolean tick() {
        if (!timeUp()) return false;
        if (voteType == VoteType.CHANGE_MAP) {
            // validation logic for yes/no map change votes
            int players = arena.getMatch().getTotalPlayers();
            int votes = voted.size();
            int yesCount = options.get("Yes");
            double participation = calculatePercentage(players, votes);
            double yesPercent = calculatePercentage(votes, yesCount);
            if ( (players <= 2 && yesCount == 2) || (players > 2 && participation > 0.5 && yesPercent > 0.5) ) {
                changeMap();
            } else {
                arena.getMatch().broadcast(String.format("%s[Vote]%s Vote failed!", ChatColor.YELLOW, ChatColor.DARK_AQUA));
            }
        }
        else if (voteType == VoteType.NEXT_MAP) {
            // validation logic for multiple-map votes to set the following map
            int players = arena.getMatch().getTotalPlayers();
            int totalVotes = 0;
            int highestVal = 0;
            String highest = null;
            for (Map.Entry<String, Integer> entry : options.entrySet()) {
                totalVotes = totalVotes + entry.getValue();
                if (highestVal == 0 || entry.getValue() > highestVal) {
                    highestVal = entry.getValue();
                    highest = entry.getKey();
                    totalVotes = totalVotes + entry.getValue();
                }
            }
            if (calculatePercentage(players, totalVotes) > 0.5 && highest != null) {
                setNextMap(highest);
            } else {
                arena.getMatch().broadcast(String.format("%s[Vote]%s Vote failed!", ChatColor.YELLOW, ChatColor.DARK_AQUA));
            }
        }
        return true;
    }


    /**
     * Kick off an immediate map change
     */
    private void changeMap() {
        arena.getMatch().broadcast(String.format("%s[Vote]%s Vote passed! Changing map to %s in 5 seconds...", ChatColor.YELLOW, ChatColor.DARK_AQUA, map.getName()));
        new BukkitRunnable() {
            public void run() {
                arena.forceMapChange(map.getFileName());
            }
        }.runTaskLater(arena.getPlugin(), 100L);
    }


    /**
     * When a vote wins, set the map that the rotation will cycle to next
     */
    private void setNextMap(String fileName) {
        arena.getRotation().setNextMap(fileName);
        arena.getMatch().broadcast(String.format("%s[Vote]%s Vote passed! The next map will be %s...", ChatColor.YELLOW, ChatColor.DARK_AQUA, map.getName()));
    }


    /**
     * Calculate the percentage of a number.
     * Returns the percentage of x that y is.
     * @param number The initial number
     * @param input The value to check against number
     * @return the percentage (0.0-1.0)
     */
    private double calculatePercentage(int number, int input) {
        double percentage;
        try {
            percentage = (double)number / (double)input;
        } catch (ArithmeticException ex) {
            percentage = 0.0;
        }
        return percentage;
    }


    /**
     * Returns true when it's time to check the vote results.
     * Votes end after one minute, or if every active player has voted.
     * Spectators are excluded by getPlayerCount().
     * @return true when the voting period should end
     */
    private boolean timeUp() {
        return System.currentTimeMillis() > (timeCreated + 60000) || (voted.size() == arena.getPlayerCount() && voted.size() > 1);
    }


    public VoteType getVoteType() {
        return voteType;
    }


    public Map<String, Integer> getOptions() {
        return options;
    }


    public long getTimeCreated() {
        return timeCreated;
    }


    public GameMap getMap() {
        return map;
    }


    public void setVoted(UUID uuid) {
        voted.add(uuid);
    }


    public boolean hasVoted(UUID uuid) {
        return voted.contains(uuid);
    }


}
