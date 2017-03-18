package io.github.redwallhp.athenagm.modules.voting;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import io.github.redwallhp.athenagm.utilities.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Handle player-initiated voting for map changes.
 */
public class VotingModule implements Module {


    private AthenaGM plugin;
    private Map<Arena, Vote> votes;
    private BukkitRunnable ticker;


    public String getModuleName() {
        return "voting";
    }


    public VotingModule(AthenaGM plugin) {
        this.plugin = plugin;
        this.votes = new HashMap<Arena, Vote>();
        this.ticker = new BukkitRunnable() {
            public void run() {
                Iterator<Vote> it = votes.values().iterator();
                while (it.hasNext()) {
                    Vote vote = it.next();
                    boolean rem = vote.tick();
                    if (rem) {
                        it.remove();
                    }
                }
            }
        };
        this.ticker.runTaskTimer(plugin, 20L, 20L);
    }


    public void unload() {
        this.ticker.cancel();
    }


    /**
     * Create a vote for an immediate map change.
     * @param arena the Arena this vote originates from
     * @param mapName the name of the map to vote for, or "random"
     * @return true if the vote was successfully started
     */
    public boolean createMapVote(Arena arena, Player player, String mapName) {

        if (!plugin.config.VOTING) {
            player.sendMessage(ChatColor.RED + "Map voting is not enabled.");
            return false;
        }

        if (votes.containsKey(arena)) {
            player.sendMessage(ChatColor.RED + "Only one vote can be active at once!");
            return false;
        }

        if (arena.getMatch().getTimer().timeLeftInSeconds() <= 120) {
            player.sendMessage(ChatColor.RED + "You cannot start a map vote this late in the match.");
            return false;
        }

        Team team = PlayerUtil.getTeamForPlayer(arena.getMatch(), player);
        if (team == null || team.isSpectator()) {
            player.sendMessage(ChatColor.RED + "Spectators cannot vote!");
            return false;
        }

        // Determine map
        GameMap map = null;
        if (mapName.equalsIgnoreCase("random")) {
            List<GameMap> mapList = arena.getRotation().getMaps();
            Random random = new Random();
            int roll = random.nextInt(mapList.size());
            map = mapList.get(roll);
        } else {
            map = arena.getRotation().getMapByFileName(mapName);
        }

        // Fail if a valid map was not specified
        if (map == null) {
            List<String> fileNames = new ArrayList<String>();
            for (GameMap m : arena.getRotation().getMaps()) {
                fileNames.add(m.getFileName());
            }
            String maps = StringUtil.joinList(", ", fileNames);
            player.sendMessage(String.format("%sMap not found!%s Valid maps: %s", ChatColor.RED, ChatColor.GRAY, maps));
            return false;
        }

        // Instantiate vote
        Vote vote = new Vote(arena, map);
        votes.put(arena, vote);
        vote.cast(player, "Yes");

        // Broadcast the vote text
        arena.getMatch().broadcast(String.format("%s[Vote]%s %s has started a vote. Do you want to switch maps to %s?", ChatColor.YELLOW, ChatColor.DARK_AQUA, player.getName(), mapName));
        String yesStr = String.format("%s/vote yes%s", ChatColor.YELLOW, ChatColor.GRAY);
        String noStr = String.format("%s/vote no%s", ChatColor.YELLOW, ChatColor.GRAY);
        arena.getMatch().broadcast(String.format("%sType %s or %s to vote!", ChatColor.GRAY, yesStr, noStr));

        return true;

    }


    /**
     * Create a vote for the map that will follow the current one
     * @param arena the Arena this vote originates from
     * @param player the Player initiating the vote
     * @return true if the vote was successfully started
     */
    public boolean createNextMapVote(Arena arena, Player player) {

        if (!plugin.config.VOTING) {
            player.sendMessage(ChatColor.RED + "Map voting is not enabled.");
            return false;
        }

        if (votes.containsKey(arena)) {
            player.sendMessage(ChatColor.RED + "Only one vote can be active at once!");
            return false;
        }

        Team team = PlayerUtil.getTeamForPlayer(arena.getMatch(), player);
        if (team == null || team.isSpectator()) {
            player.sendMessage(ChatColor.RED + "Spectators cannot vote!");
            return false;
        }

        // Pick three random maps
        List<GameMap> mapList = new ArrayList<>(arena.getRotation().getMaps());
        List<String> mapPicks = new ArrayList<>();
        mapList.remove(arena.getMatch().getMap());
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int roll = random.nextInt(mapList.size());
            mapPicks.add(mapList.get(roll).getFileName());
            mapList.remove(roll);
        }

        // Instantiate vote
        votes.put(arena, new Vote(arena, mapPicks));

        // Broadcast the vote text
        arena.getMatch().broadcast(String.format("%s[Vote]%s %s has started a vote to set the upcoming map.", ChatColor.YELLOW, ChatColor.DARK_AQUA, player.getName()));
        arena.getMatch().broadcast(String.format("%sThree random maps have been selected. Type one of the following to vote:", ChatColor.GRAY));
        for (String map : mapPicks) {
            arena.getMatch().broadcast(String.format("%s- %s/vote %s", ChatColor.GRAY, ChatColor.YELLOW, map));
        }

        return true;

    }


    /**
     * Process a player's vote
     * @param arena the arena the ballot is in
     * @param player the player casting the vote
     * @param choice the player's response
     */
    public void vote(Arena arena, Player player, String choice) {

        if (!votes.containsKey(arena)) {
            player.sendMessage(ChatColor.RED + "There is not an ongoing vote!");
            return;
        }

        Vote vote = votes.get(arena);
        Team team = PlayerUtil.getTeamForPlayer(arena.getMatch(), player);

        if (vote.hasVoted(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already voted!");
            return;
        }

        if (team == null || team.isSpectator()) {
            player.sendMessage(ChatColor.RED + "Spectators cannot vote!");
            return;
        }

        // Handle vote
        boolean success = vote.cast(player, choice);
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Your vote was cast!");
        } else {
            String options = StringUtil.joinList(", ", new ArrayList<String>(vote.getOptions().keySet()));
            player.sendMessage(String.format("%sInvalid vote!%s Valid options: %s", ChatColor.RED, ChatColor.GRAY, options));
        }

    }


}
