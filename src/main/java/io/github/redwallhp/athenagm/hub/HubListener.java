package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HubListener implements Listener {


    AthenaGM plugin;
    Hub hub;


    public HubListener(AthenaGM plugin, Hub hub) {
        this.plugin = plugin;
        this.hub = hub;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        hub.spawnPlayer(event.getPlayer());
    }


    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (hub.playerIsInHub(event.getPlayer())) {
            hub.playerSetUp(event.getPlayer());
        }
    }


}
