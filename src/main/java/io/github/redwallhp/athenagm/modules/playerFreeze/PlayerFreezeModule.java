package io.github.redwallhp.athenagm.modules.playerFreeze;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.MatchStateChangedEvent;
import io.github.redwallhp.athenagm.events.PlayerChangedTeamEvent;
import io.github.redwallhp.athenagm.matches.MatchState;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * Prevents players from moving and interacting during pre and post round
 */
public class PlayerFreezeModule implements Module {


    private AthenaGM plugin;


    public String getModuleName() {
        return "playerFreeze";
    }


    public PlayerFreezeModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    /**
     * The condition setting whether a player should be frozen
     * @param player The player to evaluate
     */
    private boolean shouldFreeze(Player player) {
        Team team = PlayerUtil.getTeamForPlayer(plugin.getArenaHandler(), player);
        return team != null && (!team.isSpectator() && team.getMatch().getState() != MatchState.PLAYING);
    }


    /**
     * Handle movement freezing
     */
    private void setMovement(Player player) {
        if (shouldFreeze(player)) {
            player.setWalkSpeed(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false));
        } else {
            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.JUMP);
        }
    }


    /**
     * Freeze/unfreeze when the MatchState changes to and from PLAYING
     */
    @EventHandler
    public void onMatchStateChanged(MatchStateChangedEvent event) {
        for (Map.Entry<Player, Team> entry: event.getMatch().getPlayerTeamMap().entrySet()) {
            setMovement(entry.getKey());
        }
    }


    /**
     * Freeze/unfreeze when changing teams
     */
    @EventHandler
    public void onPlayerChangedTeam(PlayerChangedTeamEvent event) {
        setMovement(event.getPlayer());
    }


    /**
     * Ensure no movement at all when frozen
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (shouldFreeze(event.getPlayer())) {
            if (event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ()) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Stop frozen players from using melee combat
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (shouldFreeze(player)) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Stop frozen players from firing bows
     */
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (shouldFreeze(player)) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Stop frozen players from throwing splash potions, eating, or otherwise using items
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (shouldFreeze(event.getPlayer()) &&event.getItem() != null) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }


    /**
     * Stop splash potions from splashing
     */
    @EventHandler
    public void onPotionsSplash(PotionSplashEvent event){
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            if (shouldFreeze(player)) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Block dropping items
     */
    @EventHandler
    public void onPlayerDropitem(PlayerDropItemEvent event) {
        if (shouldFreeze(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }


}
