package io.github.redwallhp.athenagm.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Builder class to easily create and execute /title commands to display messages
 * to players, as the Bukkit API presently lacks APIs for creating titles.
 * This may spam console a bit if you run it excessively, since it's executing 2-3 commands
 * as the console sender.
 */
public class TitleBuilder {


    private String title;
    private String subtitle;
    private ChatColor titleColor;
    private ChatColor subtitleColor;
    private int fadeInTicks;
    private int fadeOutTicks;
    private int displayTicks;
    private String target;


    /**
     * Create a new TitleBuilder object.
     * Default text color: white
     * Default target: all players
     * Default fade in and fade out: 10 ticks (0.5 seconds)
     * Default display length: 60 ticks (3 seconds)
     * @param title The message to be displayed to the player
     */
    public TitleBuilder(String title) {
        this.title = title;
        this.subtitle = null;
        this.titleColor = ChatColor.WHITE;
        this.subtitleColor = ChatColor.WHITE;
        this.fadeInTicks = 10; // 0.5 seconds
        this.fadeOutTicks = 10; // 0.5 seconds
        this.displayTicks = 60; // 3 seconds
        this.target = "@a"; //target all players
    }


    /**
     * Set the subtitle
     * @param subtitle A message to display below the main title
     */
    public TitleBuilder setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }


    /**
     * Set the color of the main title
     * @param titleColor The color of the main title
     */
    public TitleBuilder setTitleColor(ChatColor titleColor) {
        this.titleColor = titleColor;
        return this;
    }


    /**
     * Set the color of the subtitle
     * @param subtitleColor The color of the subtitle
     */
    public TitleBuilder setSubtitleColor(ChatColor subtitleColor) {
        this.subtitleColor = subtitleColor;
        return this;
    }


    /**
     * Set the duration of the fade in animation
     * @param fadeInTicks Fade in duration in ticks (default: 10)
     */
    public TitleBuilder setFadeInTicks(int fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
        return this;
    }


    /**
     * Set the duration of the fade out animation
     * @param fadeOutTicks Fade out duration in ticks (default: 10)
     */
    public TitleBuilder setFadeOutTicks(int fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
        return this;
    }


    /**
     * Set the duration the title will be displayed
     * @param displayTicks Duration in ticks (default: 60)
     */
    public TitleBuilder setDisplayTicks(int displayTicks) {
        this.displayTicks = displayTicks;
        return this;
    }


    /**
     * Set the title to be displayed only to one player
     * @param player Bukkit player object
     */
    public TitleBuilder setTargetPlayer(Player player) {
        this.target = String.format("@a[name=%s]", player.getName());
        return this;
    }


    /**
     * Set the title to be displayed only to one player
     * @param playerName Name of a currently online player
     */
    public TitleBuilder setTargetPlayer(String playerName) {
        this.target = String.format("@a[name=%s]", playerName);
        return this;
    }


    /**
     * Set the title to be displayed to everyone on a Scoreboard team.
     * The Scoreboard module sets the team names to match the Team objects' ID strings.
     * @param teamName Scoreboard team name, matches a Team object's getId()
     */
    public TitleBuilder setTargetTeam(String teamName) {
        this.target = String.format("@a[team=%s]", teamName);
        return this;
    }


    /**
     * Set the title to be displayed to everyone who is NOT on a Scoreboard team.
     * The Scoreboard module sets the team names to match the Team objects' ID strings.
     * @param teamName Scoreboard team name to exempt, matches a Team object's getId()
     */
    public TitleBuilder setTargetNotTeam(String teamName) {
        this.target = String.format("@a[!team=%s]", teamName);
        return this;
    }


    /**
     * Display the finished title to its target player(s)
     */
    public void publish() {
        String cmdTime = String.format("title %s times %d %d %d", target, fadeInTicks, displayTicks, fadeOutTicks);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdTime);
        if (subtitle != null) {
            String cmdSub = String.format("title %s subtitle {text:\"%s\",color:\"%s\"}", target, subtitle, subtitleColor.toString());
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdSub);
        }
        String cmdTitle = String.format("title %s title {text:\"%s\",color:\"%s\"}", target, title, titleColor.toString());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdTitle);
    }


}
