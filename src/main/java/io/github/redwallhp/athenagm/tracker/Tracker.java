package io.github.redwallhp.athenagm.tracker;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.AthenaDeathEvent;
import io.github.redwallhp.athenagm.events.PlayerDamagePlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Tracks player damage and deaths to build AthenaDeathEvents
 */
public class Tracker implements Listener {


    private AthenaGM plugin;
    private HashMap<UUID, PlayerDamagePlayerEvent> lastDamage;


    public Tracker(AthenaGM plugin) {
        this.plugin = plugin;
        this.lastDamage = new HashMap<UUID, PlayerDamagePlayerEvent>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Fire PlayerDamagePlayerEvent when a player attacks another player
     * while in an ongoing match.
     * @see PlayerDamagePlayerEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void triggerPlayerDamagePlayerEvent(EntityDamageByEntityEvent event) {

        Player victim = null;
        Player attacker = null;
        boolean ranged = false;

        // Melee
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.PLAYER)) {
            victim = (Player) event.getEntity();
            attacker = (Player) event.getDamager();
        }

        // Projectile
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile a = (Projectile) event.getDamager();
            if (a.getShooter() instanceof Player) {
                victim = (Player) event.getEntity();
                attacker = (Player) a.getShooter();
                ranged = true;
            }
        }

        // Potion
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getCause().equals(EntityDamageEvent.DamageCause.MAGIC)) {
            ThrownPotion potion = (ThrownPotion) event.getDamager();
            if (potion.getShooter() instanceof Player) {
                victim = (Player) event.getEntity();
                attacker = (Player) potion.getShooter();
                ranged = true;
            }
        }

        if (attacker == null || victim == null) return;
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(victim);
        if (arena == null) return;

        PlayerDamagePlayerEvent e = new PlayerDamagePlayerEvent(arena.getMatch(), attacker, victim, ranged, event);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled(true);
        } else {
            lastDamage.put(victim.getUniqueId(), e);
        }

    }


    /**
     * Fire AthenaDeathEvent when a player dies during a match.
     * @see AthenaDeathEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void triggerAthenaDeathEvent(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(player);
        if (arena == null) return;
        AthenaDeathEvent e;

        PlayerDamagePlayerEvent damage = lastDamage.get(player.getUniqueId());
        boolean wasPlayer = damage != null && System.currentTimeMillis() - damage.getTime() <= 7500;
        if (wasPlayer) {
            e = new AthenaDeathEvent(arena.getMatch(), damage.getVictim(), damage.getDamager(), damage, event);
        } else {
            e = new AthenaDeathEvent(arena.getMatch(), player, event);
        }

        Bukkit.getPluginManager().callEvent(e);

    }


}
