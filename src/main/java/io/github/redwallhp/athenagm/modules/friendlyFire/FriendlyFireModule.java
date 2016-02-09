package io.github.redwallhp.athenagm.modules.friendlyFire;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.arenas.Arena;
import io.github.redwallhp.athenagm.events.MatchStateChangedEvent;
import io.github.redwallhp.athenagm.events.PlayerDamagePlayerEvent;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Module to suppress friendly fire, so players don't intentionally
 * disrupt matches by killing their own team
 */
public class FriendlyFireModule implements Module {


    private AthenaGM plugin;
    private HashMap<UUID, Boolean> exemptions;


    public String getModuleName() {
        return "friendlyFire";
    }


    public FriendlyFireModule(AthenaGM plugin) {
        this.plugin = plugin;
        this.exemptions = new HashMap<UUID, Boolean>();
    }


    public void unload() {}


    /**
     * Block friendly fire
     */
    @EventHandler(priority = EventPriority.LOW)
    public void blockFriendlyFire(PlayerDamagePlayerEvent event) {
        if (event.isFriendlyFire() && !isMatchExempt(event.getMatch())) {
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


    /**
     * Clean up the exemptions list so we don't infinitely expand the HashMap
     */
    @EventHandler
    public void onMatchStateChanged(MatchStateChangedEvent event) {
        if (event.getCurrentState() == MatchState.ENDED) {
            exemptions.remove(event.getMatch().getUUID());
        }
    }


    /**
     * Set whether a given Match will allow friendly fire
     * @param match The Match
     * @param value True if friendly fire will be allowed
     */
    public void setMatchExempt(Match match, boolean value) {
        exemptions.put(match.getUUID(), value);
    }


    /**
     * Returns true if a Match allows friendly fire
     * @param match The Match to check
     */
    public boolean isMatchExempt(Match match) {
        return (exemptions.containsKey(match.getUUID()) && exemptions.get(match.getUUID()));
    }


}
