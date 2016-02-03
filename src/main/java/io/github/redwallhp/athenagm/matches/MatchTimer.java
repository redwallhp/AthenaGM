package io.github.redwallhp.athenagm.matches;


import io.github.redwallhp.athenagm.events.MatchTimerTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

public class MatchTimer implements Runnable {


    private Match match;
    private long endTime;
    private BukkitTask task;


    public MatchTimer(Match match, long seconds) {
        this.match = match;
        this.endTime = System.currentTimeMillis() + (seconds * 1000);
        task = Bukkit.getScheduler().runTaskTimer(match.getPlugin(), this, 0L, 20L);
    }


    /**
     * Runnable method called every 20 server ticks.
     * End the match when the timer is over, and emit an event on every MatchTimer tick.
     */
    public void run() {
        if (isOver()) {
            clear();
            if (match.getState() == MatchState.PLAYING) {
                match.broadcast(ChatColor.RED + "Match ended!");
                match.end();
            }
        }
        MatchTimerTickEvent event = new MatchTimerTickEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }


    /**
     * Clear the timer task
     */
    public void clear() {
        if (task != null) {
            Bukkit.getScheduler().cancelTask(task.getTaskId());
            task = null;
        }
    }


    /**
     * Time left in milliseconds
     */
    public long timeLeft() {
        return (endTime - System.currentTimeMillis());
    }


    /**
     * Time left in seconds
     */
    public long timeLeftInSeconds() {
        return timeLeft() / 1000;
    }


    /**
     * Is the timer duration over?
     */
    public boolean isOver() {
        return System.currentTimeMillis() >= endTime;
    }


    /**
     * Extend the duration of the timer
     */
    public void extendTime(long seconds) {
        this.endTime = endTime + (seconds * 1000);
    }


    /**
     * Get the Match object associated with this timer
     */
    public Match getMatch() {
        return match;
    }


    /**
     * The timestamp of the end time
     */
    public long getEndTime() {
        return endTime;
    }


}
