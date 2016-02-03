package io.github.redwallhp.athenagm.matches;

import io.github.redwallhp.athenagm.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class NextMatchCountdown extends BukkitRunnable {

    private Arena arena;
    private Match match;
    int seconds = 20;

    public NextMatchCountdown(Arena arena) {
        this.arena = arena;
        this.match = arena.getMatch();
        String map = arena.getRotation().getNextMap().getName();
        match.broadcast(String.format("%sNext map: %s. Changing in %d seconds...", ChatColor.DARK_AQUA, map, seconds));
        this.runTaskTimer(match.getPlugin(), 0L, 20L);
    }

    public void run() {
        seconds--;
        if (seconds <= 0) {
            this.cancel();
            arena.startNewMatch();
        }
    }

}
