package io.github.redwallhp.athenagm.matches;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartCountdown extends BukkitRunnable {

    private Match match;
    int ticks = 10;

    public MatchStartCountdown(Match match) {
        this.match = match;
        match.broadcast(ChatColor.DARK_AQUA + "The match will begin in 10 seconds...");
        Bukkit.getScheduler().runTaskTimer(match.getPlugin(), this, 0L, 20L);
    }

    public void run() {
        ticks--;
        if (ticks <= 0) {
            this.cancel();
            match.start();
            match.broadcast(ChatColor.GREEN + ">> GO! <<");
            return;
        }
        match.broadcast(String.format("%s>> %d <<", ChatColor.RED, ticks));
    }

}
