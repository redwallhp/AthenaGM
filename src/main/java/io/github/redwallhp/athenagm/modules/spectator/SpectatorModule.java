package io.github.redwallhp.athenagm.modules.spectator;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpectatorModule implements Module {


    private AthenaGM plugin;


    public String getModuleName() {
        return "spectatorUI";
    }


    public SpectatorModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    @EventHandler
    public void onPlayerMatchRespawn(PlayerMatchRespawnEvent event) {
        if (!isPlayerSpectator(event.getPlayer())) return;
        Inventory inventory = event.getPlayer().getInventory();
        ItemStack helpBook = new HelpBook("Help", plugin.config.NETWORK_NAME, readHelpBookFile());
        inventory.addItem(helpBook);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!isPlayerSpectator(event.getPlayer())) return;

        List<Material> allowed = new ArrayList<Material>();
        allowed.add(Material.WRITTEN_BOOK);

        // Cancel event if the player isn't using an allowed tool item
        if (event.getPlayer().getItemInHand() == null || !allowed.contains(event.getPlayer().getItemInHand().getType())) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (!isPlayerSpectator(player)) return;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isPlayerSpectator(event.getPlayer())) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isPlayerSpectator(event.getPlayer())) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!isPlayerSpectator(event.getPlayer())) return;
        if (event.getPlayer().getOpenInventory() != null) {
            event.setCancelled(true);
        } else {
            event.getPlayer().setItemInHand(event.getItemDrop().getItemStack());
            event.getItemDrop().remove();
        }
    }


    private Inventory getSpectatorInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Spectator");
        //ItemStack helpBook = new HelpBook(readHelpBookFile());
        //inventory.addItem(helpBook);
        return inventory;
    }


    private String readHelpBookFile() {
        File file = new File(plugin.getDataFolder(), "helpbook.txt");
        if (!file.exists()) return null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }
            reader.close();
            return sb.toString();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private boolean isPlayerSpectator(Player player) {
        Team team = PlayerUtil.getTeamForPlayer(plugin.getArenaHandler(), player);
        return (team != null && team.isSpectator());
    }


}
