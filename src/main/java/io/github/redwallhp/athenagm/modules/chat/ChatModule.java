package io.github.redwallhp.athenagm.modules.chat;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Limit global chat to the arena the player is in, and implement team chat.
 */
public class ChatModule implements Module {


    private AthenaGM plugin;

    public String getModuleName() {
        return "chat";
    }


    public ChatModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    /**
     * Intercept chat and apply changes.
     * 1. Limit messages to the current world, so they don't across arenas or the hub
     * 2. Apply team color to names if applicable
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        event.setCancelled(true);

        String tag;
        Team team = PlayerUtil.getTeamForPlayer(plugin.getArenaHandler(), event.getPlayer());
        if (team != null) {
            tag = String.format("<%s%s%s> ", team.getChatColor(), event.getPlayer().getName(), ChatColor.RESET);
        } else {
            tag = String.format("<%s%s%s> ", ChatColor.GRAY, event.getPlayer().getName(), ChatColor.RESET);
        }

        for (Player player : event.getPlayer().getWorld().getPlayers()) {
            player.sendMessage(tag + event.getMessage());
        }

    }


    /**
     * Send a team chat message.
     * This does the heavy lifting when called by the /t command executor.
     * @param player The sender
     * @param message The message
     * @return Whether the message was successfully sent
     */
    public boolean sendTeamMessage(Player player, String message) {

        Team team = PlayerUtil.getTeamForPlayer(plugin.getArenaHandler(), player);
        if (team == null) return false;
        String tag = String.format("[%s%s]<%s> ", team.getColoredName(), ChatColor.RESET, player.getName());

        for (Player p : team.getPlayers()) {
            p.sendMessage(tag + message);
        }

        return true;

    }


}
