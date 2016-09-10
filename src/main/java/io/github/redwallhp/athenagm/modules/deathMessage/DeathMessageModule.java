package io.github.redwallhp.athenagm.modules.deathMessage;

import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.events.AthenaDeathEvent;
import io.github.redwallhp.athenagm.modules.Module;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


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
     * Suppress regular death messages, replacing them with custom ones local to the player's Match
     */
    @EventHandler
    public void onAthenaDeath(AthenaDeathEvent event) {
        DamageCause cause = event.getPlayer().getLastDamageCause().getCause();
        if (event.isPvP()) {
            switch (cause) {
                case FALL:
                    handleGroundFall(event);
                    break;
                case PROJECTILE:
                    handleProjectile(event);
                    break;
                case VOID:
                    handleVoid(event);
                    break;
                default:
                    broadcast(event, event.getDeathEvent().getDeathMessage());
            }
        } else {
            broadcast(event, event.getDeathEvent().getDeathMessage());
        }
        event.getDeathEvent().setDeathMessage(null); //disable vanilla death broadcast
    }


    /**
     * Death messages for the player hitting the ground
     */
    private void handleGroundFall(AthenaDeathEvent event) {
        String p = event.getPlayer().getName();
        String k = event.getKiller().getName();
        DamageCause cause = event.getDamageEvent().getEntityEvent().getCause();
        if (cause.equals(DamageCause.PROJECTILE)) {
            int dist = Math.round(event.getDamageEvent().getDistance());
            broadcast(event, String.format("%s was shot off a high place by %s (%dm)", p, k, dist));
        }
        else {
            broadcast(event, String.format("%s was knocked off a high place by %s", p, k));
        }
    }


    /**
     * Death messages for the player being shot
     */
    private void handleProjectile(AthenaDeathEvent event) {
        String p = event.getPlayer().getName();
        String k = event.getKiller().getName();
        int dist = Math.round(event.getDamageEvent().getDistance());
        broadcast(event, String.format("%s was shot by %s (%dm)", p, k, dist));
    }


    /**
     * Death messages for the player falling into the void
     */
    private void handleVoid(AthenaDeathEvent event) {
        String p = event.getPlayer().getName();
        String k = event.getKiller().getName();
        DamageCause cause = event.getDamageEvent().getEntityEvent().getCause();
        if (cause.equals(DamageCause.PROJECTILE)) {
            int dist = Math.round(event.getDamageEvent().getDistance());
            broadcast(event, String.format("%s was shot into the void by %s (%dm)", p, k, dist));
        }
        else {
            broadcast(event, String.format("%s was knocked into the void by %s", p, k));
        }
    }


    /**
     * Broadcast custom death message after colorizing it
     * @param event the AthenaDeathEvent we caught
     * @param msg the message to broadcast
     */
    private void broadcast(AthenaDeathEvent event, String msg) {
        msg = ChatColor.stripColor(msg);
        msg = ChatColor.GRAY + msg;
        msg = msg.replace(event.getPlayer().getName(), event.getPlayerTeam().getChatColor() + event.getPlayer().getName() + ChatColor.GRAY);
        if (event.isPvP()) {
            msg = msg.replace(event.getKiller().getName(), event.getKillerTeam().getChatColor() + event.getKiller().getName() + ChatColor.GRAY);
        }
        event.getMatch().broadcast(msg);
    }


}
