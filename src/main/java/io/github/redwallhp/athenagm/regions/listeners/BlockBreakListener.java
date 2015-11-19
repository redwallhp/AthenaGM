package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles the cancellation of unwanted block destruction events
 */
public class BlockBreakListener implements Listener {


    private AthenaGM plugin;
    private RegionHandler regionHandler;


    public BlockBreakListener(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
        this.plugin = regionHandler.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Prevent general block destruction
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (!event.isCancelled() && region != null && !region.getFlags().isBlockDestroy()) {
            event.setCancelled(true);
            warnPlayer(event.getPlayer());
        }
    }


    /**
     * Prevent players from destroying item frames in protected regions
     */
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getEntity().getLocation());
        if (!event.isCancelled() && region != null && !region.getFlags().isBlockDestroy()) {
            event.setCancelled(true);
            if (event.getRemover() instanceof Player) {
                warnPlayer((Player) event.getRemover());
            }
        }
    }


    /**
     * Prevents buckets from being filled
     */
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlockClicked().getLocation());
        if (!event.isCancelled() && region != null && !region.getFlags().isBlockDestroy()) {
            event.setCancelled(true);
            warnPlayer(event.getPlayer());
        }
    }


    /**
     * Prevent TNT/creeper explosions from destroying blocks in protected regions
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> removalQueue = new ArrayList<Block>();
        for (Block block : event.blockList()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getLocation());
            if (!event.isCancelled() && region != null && !region.getFlags().isBlockDestroy()) {
                removalQueue.add(block);
            }
        }
        event.blockList().removeAll(removalQueue);
    }


    /**
     * Prevent pistons from extending blocks into a region that disables destruction
     */
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            if (region != null && !region.getFlags().isBlockDestroy()) {
                event.setCancelled(true);
                break;
            }
        }
    }


    /**
     * Prevent pistons from pulling blocks out of a region that prevents destruction
     */
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion region = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            if (region != null && !region.getFlags().isBlockDestroy()) {
                event.setCancelled(true);
                break;
            }
        }
    }


    /**
     * Warning message to send when an event is cancelled
     */
    private void warnPlayer(Player player) {
        String msg = "You cannot break blocks in this area.";
        player.sendMessage(String.format("%s\u26A0%s %s", ChatColor.YELLOW, ChatColor.RED, msg));
    }


}
