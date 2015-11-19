package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.util.Vector;


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
        if (!event.isCancelled() && region != null && !region.getFlags().isBlockPlace()) {
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
        if (!event.isCancelled() && region != null && !region.getFlags().isBlockPlace()) {
            event.setCancelled(true);
            warnPlayer(event.getPlayer());
        }
    }


    /**
     * Prevent pistons from extending blocks into a region
     */
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            if (region != null && !region.getFlags().isBlockPlace()) {
                event.setCancelled(true);
                break;
            }
        }
    }


    /**
     * Prevent pistons from pulling blocks out of a region
     */
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            if (region != null && !region.getFlags().isBlockPlace()) {
                event.setCancelled(true);
                break;
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
