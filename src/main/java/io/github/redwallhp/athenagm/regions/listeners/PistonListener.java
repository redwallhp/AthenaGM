package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;


public class PistonListener implements Listener {


    private AthenaGM plugin;
    private RegionHandler regionHandler;


    public PistonListener(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
        this.plugin = regionHandler.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion oldRegion = regionHandler.getApplicableRegion(block.getLocation());
            CuboidRegion newRegion = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            checkExtend(oldRegion, newRegion, event);
        }
    }


    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            CuboidRegion oldRegion = regionHandler.getApplicableRegion(block.getLocation());
            CuboidRegion newRegion = regionHandler.getApplicableRegion(block.getRelative(event.getDirection()).getLocation());
            checkRetract(oldRegion, newRegion, event);
        }
    }


    /**
     * Stop block movement in the following piston extension scenarios:
     * 1. Build allow or null -> build deny
     * 2. Destroy allow or null -> destroy deny
     * 3. Build deny -> null or build allow
     * 4. Destroy deny -> null or destroy allow
     * @param oldRegion The region the block is moving from
     * @param newRegion The region the block is moving to
     * @param event The piston event
     */
    private void checkExtend(CuboidRegion oldRegion, CuboidRegion newRegion, BlockPistonExtendEvent event) {
        // Build allow or null -> build deny
        if ( (oldRegion == null || oldRegion.allows("build")) && (newRegion != null && !newRegion.allows("build")) ) {
            event.setCancelled(true);
        }
        // Destroy allow or null -> destroy deny
        if ( (oldRegion == null || oldRegion.allows("destroy")) && (newRegion != null && !newRegion.allows("destroy")) ) {
            event.setCancelled(true);
        }
        // Build deny -> null or build allow
        if ( (oldRegion != null && !oldRegion.allows("build")) && (newRegion == null || newRegion.allows("build")) ) {
            event.setCancelled(true);
        }
        // Destroy deny -> null or destroy allow
        if ( (oldRegion != null && !oldRegion.allows("destroy")) && (newRegion == null || newRegion.allows("destroy")) ) {
            event.setCancelled(true);
        }
    }


    /**
     * Stop block movement in the following piston retraction scenarios:
     * 1. Build deny -> null or build allow
     * 2. Destroy deny -> null or destroy allow
     * 3. Build allow or null -> build deny
     * 4. Destroy allow or null -> destroy deny
     * @param oldRegion The region the block is moving from
     * @param newRegion The region the block is moving to
     * @param event The piston event
     */
    private void checkRetract(CuboidRegion oldRegion, CuboidRegion newRegion, BlockPistonRetractEvent event) {
        // Build deny -> null or build allow
        if ( (oldRegion != null && !oldRegion.allows("build")) && (newRegion == null || newRegion.allows("build")) ) {
            event.setCancelled(true);
        }
        // Destroy deny -> null or destroy allow
        if ( (oldRegion != null && !oldRegion.allows("destroy")) && (newRegion == null || newRegion.allows("destroy")) ) {
            event.setCancelled(true);
        }
        // Build allow or null -> build deny
        if ( (oldRegion == null || oldRegion.allows("build")) && (newRegion != null && !newRegion.allows("build")) ) {
            event.setCancelled(true);
        }
        // Destroy allow or null -> destroy deny
        if ( (oldRegion == null || oldRegion.allows("destroy")) && (newRegion != null && !newRegion.allows("destroy")) ) {
            event.setCancelled(true);
        }
    }


}
