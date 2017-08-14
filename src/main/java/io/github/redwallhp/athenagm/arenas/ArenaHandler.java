package io.github.redwallhp.athenagm.arenas;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.configuration.ConfiguredArena;
import io.github.redwallhp.athenagm.maps.MapLoader;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArenaHandler {


    private AthenaGM plugin;
    private Map<String, Arena> arenas;


    public ArenaHandler(AthenaGM plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        MapLoader.cleanUpWorldInstances(plugin.getMatchesDirectory());
        loadArenas();
        plugin.getServer().getPluginManager().registerEvents(new ArenaListener(this), plugin);
    }


    private void loadArenas() {
        for (ConfiguredArena ca : plugin.config.ARENAS.values()) {
            Arena arena = new Arena(this, ca);
            this.arenas.put(arena.getId(), arena);
        }
    }


    public AthenaGM getPluginInstance() {
        return this.plugin;
    }


    public Set<Arena> getArenas() {
        return new HashSet<>(this.arenas.values());
    }


    public Arena getArenaForPlayer(Player player) {
        for (Arena arena : this.arenas.values()) {
            if (player.getWorld().equals(arena.getWorld())) {
                return arena;
            }
        }
        return null;
    }


    public Arena getArenaById(String id) {
        if (this.arenas.containsKey(id)) {
            return this.arenas.get(id);
        }
        return null;
    }


}
