package io.github.redwallhp.athenagm.modules.spectator;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.PlayerMatchRespawnEvent;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.BookBuilder;
import io.github.redwallhp.athenagm.utilities.ItemUtil;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.*;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.File;
import java.util.*;

public class SpectatorModule implements Module {


    private AthenaGM plugin;
    private HashMap<Player, Integer> compassIndex;
    private ItemStack helpBookItem;


    public String getModuleName() {
        return "spectator";
    }


    public SpectatorModule(AthenaGM plugin) {
        this.plugin = plugin;
        this.compassIndex = new HashMap<Player, Integer>();
        this.helpBookItem = getHelpBookItem();
    }


    public void unload() {}


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleSpectatorMode(PlayerMatchRespawnEvent event) {

        Player player = event.getPlayer();
        Match match = plugin.getArenaHandler().getArenaForPlayer(player).getMatch();
        Team playerTeam = PlayerUtil.getTeamForPlayer(match, player);

        // Set spectator/non-spectator attributes
        if (playerTeam != null && playerTeam.isSpectator()) {
            player.setGameMode(GameMode.CREATIVE);
            player.spigot().setCollidesWithEntities(false);
            player.setCanPickupItems(false);
            this.setPlayerVisibility(player, true, match);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            player.spigot().setCollidesWithEntities(true);
            player.setCanPickupItems(true);
            this.setPlayerVisibility(player, false, match);
        }

    }


    @EventHandler
    public void giveSpectatorKit(PlayerMatchRespawnEvent event) {

        if (!isPlayerSpectator(event.getPlayer())) return;
        Inventory inventory = event.getPlayer().getInventory();

        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.BLUE + "Teleport");
        compass.setItemMeta(compassMeta);
        inventory.addItem(compass);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(ChatColor.GOLD + "Select Team");
        helmet.setItemMeta(helmetMeta);
        inventory.addItem(helmet);

        inventory.addItem(this.helpBookItem);

        ItemStack exitDoor = new ItemStack(Material.WOOD_DOOR, 1);
        ItemMeta exitDoorMeta = exitDoor.getItemMeta();
        exitDoorMeta.setDisplayName(ChatColor.AQUA + "Return to Minigames Hub");
        exitDoor.setItemMeta(exitDoorMeta);
        inventory.setItem(8, exitDoor);

    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!isPlayerSpectator(event.getPlayer())) return;

        // Block all pressure plate events early
        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
            return;
        }

        // Player is using the Help Book
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.WRITTEN_BOOK) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ItemUtil.getInteractiveBlocks().contains(event.getClickedBlock().getType())) {
                event.setCancelled(true);
            }
            return;
        }

        // Player is using team selector helmet
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.LEATHER_HELMET) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            event.getPlayer().openInventory(getTeamPickerInventory(event.getPlayer()));
            return;
        }

        // Player is using the navigation compass
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.COMPASS) {
            event.setCancelled(true);
            doCompassTeleport(event);
            return;
        }

        // Player is using the exit door
        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.WOOD_DOOR) {
            event.setCancelled(true);
            Bukkit.dispatchCommand(event.getPlayer(), "hub");
            return;
        }

        // Cancel all other interact events
        event.setCancelled(true);

    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (!isPlayerSpectator(player)) return;
        if (item != null) {
            if (event.getInventory().getTitle().equals("Select Team")) {
                if (item.getType().equals(Material.LEATHER_HELMET)) {
                    String teamName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase();
                    event.setCancelled(true);
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 2);
                    Bukkit.dispatchCommand(player, String.format("team %s", teamName));
                    return;
                }
                if (item.getType().equals(Material.STONE_BUTTON)) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 2);
                    Bukkit.dispatchCommand(player, "autojoin");
                    return;
                }
            }
        }
        event.setCancelled(true);
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


    private Inventory getTeamPickerInventory(Player player) {
        Match match = plugin.getArenaHandler().getArenaForPlayer(player).getMatch();
        Inventory inventory = Bukkit.createInventory(null, 9, "Select Team");
        for (Team team : match.getTeams().values()) {
            if (!team.isSpectator()) {
                ItemStack item = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                meta.setColor(ItemUtil.getColorFormString(team.getColor()));
                String name = String.format("%s%s", team.getChatColor(), team.getName());
                meta.setDisplayName(name);
                meta.setLore(Arrays.asList(String.format("%d/%d", team.getPlayers().size(), team.getSize())));
                item.setItemMeta(meta);
                inventory.addItem(item);
            }
        }
        ItemStack item = new ItemStack(Material.STONE_BUTTON, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Auto Assign");
        item.setItemMeta(meta);
        inventory.addItem(item);
        return inventory;
    }


    private void doCompassTeleport(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK  || event.getAction() == Action.LEFT_CLICK_AIR) {
            // Teleport to the next player
            Match match = plugin.getArenaHandler().getArenaForPlayer(event.getPlayer()).getMatch();
            List<Player> players = new ArrayList<Player>();
            for (Team team : match.getTeams().values()) {
                if (team.isSpectator()) continue;
                for (Player player : team.getPlayers()) {
                    players.add(player);
                }
            }
            Integer nextIndex = 0;
            if (this.compassIndex.containsKey(event.getPlayer())) {
                nextIndex = this.compassIndex.get(event.getPlayer()) + 1;
                if (nextIndex > players.size() - 1) nextIndex = 0;
                this.compassIndex.put(event.getPlayer(), nextIndex);
            } else {
                this.compassIndex.put(event.getPlayer(), 0);
            }
            if (players.size() > 0) {
                event.getPlayer().teleport(players.get(nextIndex).getLocation());
                event.getPlayer().sendMessage(String.format("%sTeleporting to %s", ChatColor.GRAY, players.get(nextIndex).getName()));
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            // Teleport back to spawn point
            Match match = plugin.getArenaHandler().getArenaForPlayer(event.getPlayer()).getMatch();
            event.getPlayer().teleport(match.getSpawnPoint(event.getPlayer()));
        }
    }


    public ItemStack getHelpBookItem() {
        File file = new File(plugin.getDataFolder(), "helpbook.txt");
        BookBuilder bookBuilder = new BookBuilder("Help");
        bookBuilder.setDefaultContents("This book will be populated with the contents of a &lhelpbook.txt&0 file in the plugin directory.");
        bookBuilder.setPagesFromFile(file);
        this.helpBookItem = bookBuilder.getBook(); //cache to avoid hitting the disk all the time
        return bookBuilder.getBook();
    }


    private void setPlayerVisibility(Player player, boolean playerIsSpectator, Match match) {

        HashMap<Player, Team> teamMap = match.getPlayerTeamMap();

        // Handle respawning player
        for (Map.Entry<Player, Team> entry : teamMap.entrySet()) {
            Player lPlayer = entry.getKey();
            Team lTeam = entry.getValue();
            if (playerIsSpectator) {
                // Show all other players to the player respawning
                player.showPlayer(lPlayer);
            } else {
                // Hide spectators from the player respawning, but show all others
                if (lTeam.isSpectator()) {
                    player.hidePlayer(lPlayer);
                } else {
                    player.showPlayer(lPlayer);
                }
            }
        }

        // Handle other players
        for (Map.Entry<Player, Team> entry : teamMap.entrySet()) {
            Player lPlayer = entry.getKey();
            Team lTeam = entry.getValue();
            if (playerIsSpectator) {
                // Hide the respawning player from others who are not on the spectator team
                if (lTeam.isSpectator()) {
                    lPlayer.showPlayer(player);
                } else {
                    lPlayer.hidePlayer(player);
                }
            } else {
                lPlayer.showPlayer(player);
            }
        }

    }


    private boolean isPlayerSpectator(Player player) {
        Team team = PlayerUtil.getTeamForPlayer(plugin.getArenaHandler(), player);
        return (team != null && team.isSpectator());
    }


}
