package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class HubListener implements Listener {


    AthenaGM plugin;
    Hub hub;


    public HubListener(AthenaGM plugin, Hub hub) {
        this.plugin = plugin;
        this.hub = hub;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Spawn the player when they join the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        hub.spawnPlayer(event.getPlayer());
    }


    /**
     * Ensure that a player coming into the Hub from another world always gets reset properly
     */
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (hub.hasPlayer(event.getPlayer())) {
            hub.playerSetUp(event.getPlayer());
        }
    }


    /**
     * If a player manages to die in the Hub, ensure they respawn properly
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (hub.hasPlayer(event.getPlayer())) {
            event.setRespawnLocation(hub.getWorld().getSpawnLocation());
            hub.playerSetUp(event.getPlayer());
        }
    }


}
