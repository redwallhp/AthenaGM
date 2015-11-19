package io.github.redwallhp.athenagm.modules.friendlyFire;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.PlayerDamagePlayerEvent;
import io.github.redwallhp.athenagm.matches.Team;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.utilities.PlayerUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Module to suppress friendly fire, so players don't intentionally
 * disrupt matches by killing their own team
 */
public class FriendlyFireModule implements Module {


    private AthenaGM plugin;


    public String getModuleName() {
        return "friendlyFire";
    }


    public FriendlyFireModule(AthenaGM plugin) {
        this.plugin = plugin;
    }


    public void unload() {}


    /**
     * Block friendly fire
     */
    @EventHandler(priority = EventPriority.LOW)
    public void blockFriendlyFire(PlayerDamagePlayerEvent event) {
        if (event.isFriendlyFire()) {
            event.setCancelled(true);
        }
    }


    /**
     * Stop negative effect splash potions from hurting a player's own team
     */
    @EventHandler(priority = EventPriority.LOW)
    public void blockFriendlyFirePotions(PotionSplashEvent event) {

        List<PotionEffectType> blacklist = new ArrayList<PotionEffectType>();
        blacklist.add(PotionEffectType.HARM);
        blacklist.add(PotionEffectType.POISON);
        blacklist.add(PotionEffectType.WEAKNESS);
        blacklist.add(PotionEffectType.SLOW);
        blacklist.add(PotionEffectType.INVISIBILITY);

        if (!(event.getPotion().getShooter() instanceof Player)) return;
        Player damager = (Player) event.getPotion().getShooter();
        Arena arena = plugin.getArenaHandler().getArenaForPlayer(damager);

        if (arena != null) {
            Team dTeam = PlayerUtil.getTeamForPlayer(arena.getMatch(), damager);
            for (LivingEntity ent : event.getAffectedEntities()) {
                if (!(ent instanceof Player)) continue;
                if (dTeam == PlayerUtil.getTeamForPlayer(arena.getMatch(), (Player) ent)) {
                    for (PotionEffect effect : event.getPotion().getEffects()) {
                        if (blacklist.contains(effect.getType())) event.setIntensity(ent, 0);
                    }
                }
            }
        }

    }


}
