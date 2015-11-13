package io.github.redwallhp.athenagm.modules.kits;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.maps.MapInfoKitItem;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Handle player kits on respawn
 */
public class KitsModule implements Module {


    private AthenaGM plugin;


    public String getModuleName() {
        return "kits";
    }


    public KitsModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    @EventHandler
    public void giveKitOnRespawn(PlayerMatchRespawnEvent event) {

        Player player = event.getPlayer();
        Match match = plugin.getArenaHandler().getArenaForPlayer(player).getMatch();
        Team playerTeam = PlayerUtil.getTeamForPlayer(match, player);

        if (playerTeam == null || playerTeam.isSpectator()) return; //skip spectators and nonexistant teams

        Inventory inventory = player.getInventory();
        for (MapInfoKitItem item : playerTeam.getKitItems()) {
            if (item.getInventorySlot() < 40 && item.getInventorySlot() >= 0) {
                inventory.setItem(item.getInventorySlot(), item.getItem());
            }
        }
        player.updateInventory();

    }


}
