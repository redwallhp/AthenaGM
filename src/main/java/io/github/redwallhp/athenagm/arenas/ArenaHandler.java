package io.github.redwallhp.athenagm.arenas;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.configuration.ConfiguredArena;
import io.github.redwallhp.athenagm.maps.MapLoader;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaHandler {


    private AthenaGM plugin;
    private List<Arena> arenas;


    public ArenaHandler(AthenaGM plugin) {
        this.plugin = plugin;
        this.arenas = new ArrayList<Arena>();
        MapLoader.cleanUpWorldInstances(plugin.getMatchesDirectory());
        loadArenas();
        plugin.getServer().getPluginManager().registerEvents(new ArenaListener(this), plugin);
    }


    private void loadArenas() {
        for (ConfiguredArena ca : plugin.config.ARENAS.values()) {
            Arena arena = new Arena(this, ca);
            this.arenas.add(arena);
        }
    }


    public AthenaGM getPluginInstance() {
        return this.plugin;
    }


    public List<Arena> getArenas() {
        return this.arenas;
    }


    public Arena getArenaForPlayer(Player player) {
        for (Arena arena : this.arenas) {
            if (player.getWorld().equals(arena.getWorld())) {
                return arena;
            }
        }
        return null;
    }


    public Arena getArenaById(String id) {
        for (Arena arena : this.arenas) {
            if (arena.getId().equalsIgnoreCase(id)) {
                return arena;
            }
        }
        return null;
    }


}
