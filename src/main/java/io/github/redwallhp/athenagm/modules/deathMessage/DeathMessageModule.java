package io.github.redwallhp.athenagm.modules.deathMessage;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.modules.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessageModule implements Module {


    private AthenaGM plugin;


    public String getModuleName() {
        return "deathMessage";
    }


    public DeathMessageModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    /**
     * Suppress regular death messages, replacing them with ones local to the player's Match
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(event.getEntity());
        if (arena != null) {
            arena.getMatch().broadcast(event.getDeathMessage());
            event.setDeathMessage(null);
        }
    }


}
