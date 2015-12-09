package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import io.github.redwallhp.athenagm.utilities.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Handles player use/interact
 */
public class PlayerInteractListener implements Listener {


    private AthenaGM plugin;
    private RegionHandler regionHandler;


    public PlayerInteractListener(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
        this.plugin = regionHandler.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Prevent the player from interacting with blocks (e.g. buttons, levers, doors)
     * Only checks click action, not PHYSICAL, so pressure plates are always allowed.
     * This is both a mostly-desirable behavior and a workaround for the fact that PHYSICAL
     * actions fire continuously while a pressure plate is depressed...which makes for a flood
     * of warnings to the chat pane.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null) return; // Not interacting with a block
        if (event.getItem() != null && event.getItem().getType().isBlock()) return; // Is placing a block

        CuboidRegion region = regionHandler.getApplicableRegion(event.getClickedBlock().getLocation());
        if (region == null || region.allows("interact")) return; // Interact is allowed

        // Cancel the event and warn the player if it's an interactive block being clicked
        if (ItemUtil.getInteractiveBlocks().contains(event.getClickedBlock().getType())) {
            event.setCancelled(true);
            warnPlayer(event.getPlayer());
        }

    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        CuboidRegion region = regionHandler.getApplicableRegion(event.getRightClicked().getLocation());
        if (region == null || region.allows("interact")) return; // Interact is allowed

        event.setCancelled(true);
        warnPlayer(event.getPlayer());

    }


    /**
     * Warning message to send when an event is cancelled
     */
    private void warnPlayer(Player player) {
        String msg = "You cannot interact with things in this area.";
        player.sendMessage(String.format("%s\u26A0%s %s", ChatColor.YELLOW, ChatColor.RED, msg));
    }


}
