package io.github.redwallhp.athenagm.regions.listeners;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

/**
 * Handles automatic environmental events, like ice melting
 */
public class EnvironmentalListener implements Listener {


    private AthenaGM plugin;
    private RegionHandler regionHandler;


    public EnvironmentalListener(RegionHandler regionHandler) {
        this.regionHandler = regionHandler;
        this.plugin = regionHandler.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Prevent snow layers or ice blocks from melting if the "melt" flag is false
     */
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {

        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (region == null || region.allows("melt")) return; // snow/ice melting is allowed

        if (event.getBlock().getType().equals(Material.ICE) || event.getBlock().getType().equals(Material.SNOW)) {
            event.setCancelled(true);
        }

    }


    /**
     * Prevent vines from spreading if the vine_spread flag is set to false
     */
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {

        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (region == null || region.allows("vine_spread")) return; // vines are allowed to spread

        if (event.getBlock().getType().equals(Material.VINE)) {
            event.setCancelled(true);
        }

    }


    /**
     * Prevent leaves from decaying if leaf_decay is set to false
     */
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (region == null || region.allows("leaf_decay")) return; // leaves are allowed to decay
        event.setCancelled(true);
    }


    /**
     * Prevent fire from spreading if the fire_spread flag is false.
     * Also, prevent a player from igniting a block with flint and steel or a fireball if they lack build permission.
     * @param event
     */
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {

        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (region == null) return;

        // block fire from spreading
        if (!region.allows("fire_spread")) {
            if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA) {
                event.setCancelled(true);
            }
        }

        // block ignition via lighter or fireball if a region doesn't allow building
        if (!region.allows("build")) {
            if (event.getPlayer() != null) {
                if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL || event.getCause() == BlockIgniteEvent.IgniteCause.FIREBALL) {
                    event.setCancelled(true);
                }
            }
        }

    }


    /**
     * If fire_spread is false, prevent blocks from being destroyed by fire
     */
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        CuboidRegion region = regionHandler.getApplicableRegion(event.getBlock().getLocation());
        if (region == null || region.allows("fire_spread")) return; // fire spread is allowed
        event.setCancelled(true);
    }


}
