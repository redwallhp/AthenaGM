package io.github.redwallhp.athenagm.modules.spectator;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.ItemUtil;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
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
        ItemStack helpBook = new HelpBook("Help", readHelpBookFile());
        inventory.addItem(helpBook);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!isPlayerSpectator(event.getPlayer())) return;

        // Player is using the Help Book
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.WRITTEN_BOOK) {
            // No interacting with pressure plates, buttons, etc
            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ItemUtil.getInteractiveBlocks().contains(event.getClickedBlock().getType())) {
                event.setCancelled(true);
            }
            return;
        }

        event.setCancelled(true);

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


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Bukkit.dispatchCommand(damager, "kick redwall_hp"); //testing
            if (isPlayerSpectator(damager)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!isPlayerSpectator(event.getPlayer())) return;
        event.setCancelled(true);
    }


    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            if (isPlayerSpectator(player)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player) {
            Player player = (Player) event.getAttacker();
            if (isPlayerSpectator(player)) {
                event.setCancelled(true);
            }
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
