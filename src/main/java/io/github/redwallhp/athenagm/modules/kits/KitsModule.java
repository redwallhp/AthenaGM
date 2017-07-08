package io.github.redwallhp.athenagm.modules.kits;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.AthenaDeathEvent;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.maps.MapInfoKitItem;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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


    /**
     * When a player respawns, give them the appropriate kit from the map configuration
     */
    @EventHandler
    public void giveKitOnRespawn(PlayerMatchRespawnEvent event) {

        Player player = event.getPlayer();
        Match match = plugin.getArenaHandler().getArenaForPlayer(player).getMatch();
        Team playerTeam = PlayerUtil.getTeamForPlayer(match, player);
        int roll = (int) (Math.random() * 100);

        if (playerTeam == null || playerTeam.isSpectator()) return; //skip spectators and nonexistant teams

        if (playerTeam.getKitItems() != null) {
            Inventory inventory = player.getInventory();
            for (MapInfoKitItem item : playerTeam.getKitItems()) {
                if (item.getInventorySlot() < 41 && item.getInventorySlot() >= 0) {
                    if (roll > item.getChance()) continue; //handle random chance items
                    inventory.setItem(item.getInventorySlot(), item.getItem());
                }
            }
            player.updateInventory();
        }

    }


    /**
     * Blacklist item drops flagged as "drop: false" in the kit definition
     */
    @EventHandler
    public void onPlayerMatchDeathEvent(AthenaDeathEvent event) {

        if (event.getPlayerTeam().getKitItems() == null) return;

        List<ItemStack> blacklist = new ArrayList<ItemStack>();
        for (MapInfoKitItem kitItem : event.getPlayerTeam().getKitItems()) {
            if (!kitItem.dropOnDeath()) blacklist.add(kitItem.getItem());
        }

        ListIterator<ItemStack> it = event.getDrops().listIterator();
        while (it.hasNext()) {
            ItemStack item = it.next();
            for (ItemStack blacklisted : blacklist) {
                if (blacklisted.getType().equals(item.getType())) {
                    it.remove();
                    break; //only remove one item per pass, to prevent IllegalStateException
                }
            }
        }

    }


}
