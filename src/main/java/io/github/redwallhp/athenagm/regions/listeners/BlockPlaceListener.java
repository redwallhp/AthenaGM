package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.Dispenser;


/**
 * Handles the cancellation of unwanted block placement events
 */
public class BlockPlaceListener implements Listener {


    private AthenaGM plugin;
    private RegionHandler regionHandler;


    public BlockPlaceListener(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
        this.plugin = regionHandler.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Prevent general block placement
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlockPlaced().getLocation());
        if (!event.isCancelled() && region != null && !region.allows("build")) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            warnPlayer(event.getPlayer());
        }
    }


    /**
     * Prevent bucket usage
     */
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlockClicked().getLocation());
        if (!event.isCancelled() && region != null && !region.allows("build")) {
            event.setCancelled(true);
            warnPlayer(event.getPlayer());
        }
    }


    /**
     * Prevent pistons from extending blocks into a region that disables placement
     */
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            if (region != null && !region.allows("build")) {
                event.setCancelled(true);
                break;
            }
        }
    }


    /**
     * Prevent pistons from pulling blocks out of a region that disables placement
     */
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            if (region != null && !region.allows("build")) {
                event.setCancelled(true);
                break;
            }
        }
    }


    /**
     * Prevent a dispenser from placing liquid in a region that disables placement
     */
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        Dispenser dispenser = (Dispenser)event.getBlock().getState().getData();
        Block targetBlock = event.getBlock().getRelative(dispenser.getFacing());
        if (event.getItem().getType() == Material.LAVA_BUCKET || event.getItem().getType() == Material.WATER_BUCKET) {
            CuboidRegion region = regionHandler.getApplicableRegion(targetBlock.getLocation());
            if (region != null && !region.allows("build")) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Keep liquids from flowing into a protected region from outside
     */
    @EventHandler
    public void onBLiquidFlow(BlockFromToEvent event) {
        CuboidRegion toRegion = regionHandler.getApplicableRegion(event.getToBlock().getLocation());
        CuboidRegion fromRegion = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (!event.isCancelled() && toRegion != null) {
            if (!toRegion.allows("build") && (fromRegion == null || !fromRegion.allows("build"))) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Warning message to send when an event is cancelled
     */
    private void warnPlayer(Player player) {
        String msg = "You cannot place blocks in this area.";
        player.sendMessage(String.format("%s\u26A0%s %s", ChatColor.YELLOW, ChatColor.RED, msg));
    }


}
