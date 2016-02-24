package io.github.redwallhp.athenagm.modules.worldBorder;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.MatchCreateEvent;
import io.github.redwallhp.athenagm.maps.GameMap;
import io.github.redwallhp.athenagm.modules.Module;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;

/**
 * World border module. Keeps players from wandering too far away.
 * Most maps shouldn't need this, as there's only so far you can go in a void world, but
 * it's here if you need it.
 * You can adjust the border dimensions after match creation by calling the Bukkit API's
 * WorldBorder methods.
 * @see org.bukkit.WorldBorder
 */
public class WorldBorderModule implements Module {


    private AthenaGM plugin;


    public String getModuleName() {
        return "worldBorder";
    }


    public WorldBorderModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    @EventHandler
    public void onMatchCreate(MatchCreateEvent event) {
        GameMap map = event.getMatch().getMap();
        if (map.getWorldBorder().isEnabled()) {
            WorldBorder wb = event.getMatch().getWorld().getWorldBorder();
            wb.setCenter(map.getWorldBorder().getCenterX(), map.getWorldBorder().getCenterY());
            wb.setSize(map.getWorldBorder().getRadius()*2);
        }
    }


}
