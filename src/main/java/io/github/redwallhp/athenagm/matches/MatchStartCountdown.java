package io.github.redwallhp.athenagm.matches;


import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartCountdown extends BukkitRunnable {

    private Match match;
    int ticks = 10;

    public MatchStartCountdown(Match match) {
        this.match = match;
        match.broadcast(ChatColor.RED + "The match will begin in 10 seconds...");
        this.runTaskTimer(match.getPlugin(), 0L, 20L);
    }

    public void run() {
        ticks--;
        if (ticks <= 0) {
            this.cancel();
            match.start();
            match.broadcast(ChatColor.GREEN + ">> GO! <<");
            match.playSound(Sound.NOTE_PIANO, 0.67f);
            return;
        }
        match.broadcast(String.format("%s>> %d <<", ChatColor.RED, ticks));
        match.playSound(Sound.NOTE_PIANO, 0.53f);
    }

}
