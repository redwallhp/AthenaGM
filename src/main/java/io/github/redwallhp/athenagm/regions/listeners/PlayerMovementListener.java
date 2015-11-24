package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;


/**
 * Handles region events triggered by a player's movement
 */
public class PlayerMovementListener implements Listener {


    private AthenaGM plugin;
    private RegionHandler regionHandler;


    public PlayerMovementListener(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
        this.plugin = regionHandler.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.isCancelled()) {
            CuboidRegion toRegion = regionHandler.getApplicableRegion(event.getTo());
            CuboidRegion fromRegion = regionHandler.getApplicableRegion(event.getFrom());
            handleEntryDeny(event, toRegion);
            handleExitDeny(event, fromRegion);
            handleTeamRestricted(event, toRegion);
            handleEntryHail(event, toRegion);
            handleExitHail(event, fromRegion);
            handleVelocity(event, toRegion);
        }
    }


    /**
     * If the deny_entry flag is set to true, knock players back when they try to enter the region.
     */
    private void handleEntryDeny(PlayerMoveEvent event, CuboidRegion toRegion) {
        if (toRegion != null && !toRegion.getFlags().getBoolean("entry")) {
            if (!toRegion.contains(event.getFrom())) {
                knockBack(event.getPlayer(), event.getTo().toVector(), event.getFrom().toVector(), 1.1f);
                warnPlayer(event.getPlayer(), "You cannot go there.");
            }
        }
    }


    /**
     * If the deny_exit flag is set to true, prevent players from exiting the region.
     */
    private void handleExitDeny(PlayerMoveEvent event, CuboidRegion fromRegion) {
        if (fromRegion != null && !fromRegion.getFlags().getBoolean("exit")) {
            if (!fromRegion.contains(event.getTo())) {
                knockBack(event.getPlayer(), event.getTo().toVector(), event.getFrom().toVector(), 1.1f);
                warnPlayer(event.getPlayer(), "You cannot go there.");
            }
        }
    }


    /**
     * If the team_restricted flag is set to a team ID string, only players in that team will
     * be able to enter (except for spectators, which are exempt). All others will be
     * knocked back and shown a message.
     */
    private void handleTeamRestricted(PlayerMoveEvent event, CuboidRegion toRegion) {
        if (toRegion != null) {
            String teamID = toRegion.getFlags().getString("team_restricted");
            if (teamID != null && !teamID.equals("")) {
                if (!toRegion.contains(event.getFrom())) {
                    Team playerTeam = PlayerUtil.getTeamForPlayer(plugin.getArenaHandler(), event.getPlayer());
                    if (playerTeam != null && !playerTeam.getId().equals(teamID) && !playerTeam.isSpectator()) {
                        knockBack(event.getPlayer(), event.getTo().toVector(), event.getFrom().toVector(), 1.1f);
                        Team regionTeam = playerTeam.getMatch().getTeams().get(teamID);
                        warnPlayer(event.getPlayer(), String.format("Only %s%s team can go there.", regionTeam.getColoredName(), ChatColor.RED));
                    }
                }
            }
        }
    }


    /**
     * Print a message to the player when they enter a region, if one is set in
     * the entry_hail flag.
     */
    private void handleEntryHail(PlayerMoveEvent event, CuboidRegion toRegion) {
        if (toRegion != null) {
            String entryHail = toRegion.getFlags().getString("entry_hail");
            if (entryHail != null && !entryHail.equals("")) {
                if (!toRegion.contains(event.getFrom())) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "\u25B6 " + entryHail));
                }
            }
        }
    }


    /**
     * Print a message to the player when they exit a region, if one is set in
     * the exit_hail flag.
     */
    private void handleExitHail(PlayerMoveEvent event, CuboidRegion fromRegion) {
        if (fromRegion != null) {
            String exitHail = fromRegion.getFlags().getString("exit_hail");
            if (exitHail != null && !exitHail.equals("")) {
                if (!fromRegion.contains(event.getTo())) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "\u25B6 " + exitHail));
                }
            }
        }
    }


    /**
     * Accelerate the player with a given velocity multiplier, if one is set in the
     * velocity flag.
     */
    private void handleVelocity(PlayerMoveEvent event, CuboidRegion toRegion) {
        if (toRegion != null && toRegion.getFlags().getDouble("velocity") != null) {
            if (!toRegion.contains(event.getFrom())) {
                Vector from = event.getFrom().toVector();
                Vector to = event.getTo().toVector();
                from.setY(from.getY()-0.25);
                Vector dir = to.subtract(from).normalize();
                Double multiplier = toRegion.getFlags().getDouble("velocity");
                event.getPlayer().setVelocity(dir.multiply(multiplier));
            }
        }
    }


    /**
     * Knock the player back, based on a multiple of their previous forward velocity.
     * Calculates a backward trajectory by subtracting the "to" vector from the "from vector,
     * and multiplies it to create a knockback effect.
     * @param player The player to knock back
     * @param to The block the player was moving to
     * @param from The block the player was moving from
     */
    private void knockBack(Player player, Vector to, Vector from, float multiplier) {
        Vector dir = from.subtract(to).normalize();
        player.setVelocity(dir.multiply(multiplier));
    }


    /**
     * Warning message to send when an event is cancelled
     */
    private void warnPlayer(Player player, String msg) {
        player.sendMessage(String.format("%s\u26A0%s %s", ChatColor.YELLOW, ChatColor.RED, msg));
    }


}
