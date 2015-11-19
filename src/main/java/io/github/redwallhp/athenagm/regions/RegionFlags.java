package io.github.redwallhp.athenagm.regions;


import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class RegionFlags {


    private boolean blockPlace;
    private boolean blockDestroy;
    private boolean interact;
    private boolean denyEntry;
    private boolean denyExit;
    private String teamRestricted;
    private String entryHail;
    private String exitHail;
    private List<String> tags;


    public RegionFlags() {
        blockPlace = true;
        blockDestroy = true;
        interact = true;
        denyEntry = false;
        denyExit = false;
        teamRestricted = null;
        entryHail = null;
        entryHail = null;
        tags = new ArrayList<String>();
    }


    /**
     * Whether players can place blocks in this region
     */
    public boolean isBlockPlace() {
        return blockPlace;
    }


    /**
     * Set whether players can place blocks in this region
     */
    public void setBlockPlace(boolean blockPlace) {
        this.blockPlace = blockPlace;
    }


    /**
     * Whether players can destroy blocks in this region
     */
    public boolean isBlockDestroy() {
        return blockDestroy;
    }


    /**
     * Set whether playesr can destroy blocks in this region
     */
    public void setBlockDestroy(boolean blockDestroy) {
        this.blockDestroy = blockDestroy;
    }


    /**
     * Whether players can interact/use objects in this region
     */
    public boolean isInteract() {
        return interact;
    }


    /**
     * Set whether players can interact/use objects in this region
     */
    public void setInteract(boolean interact) {
        this.interact = interact;
    }


    /**
     * Whether this region denies entry to players
     */
    public boolean isDenyEntry() {
        return denyEntry;
    }


    /**
     * Set whether this region denies entry to players
     */
    public void setDenyEntry(boolean denyEntry) {
        this.denyEntry = denyEntry;
    }


    /**
     * Whether this region prevents players from leaving
     */
    public boolean isDenyExit() {
        return denyExit;
    }


    /**
     * Set whether this region prevents players from leaving
     */
    public void setDenyExit(boolean denyExit) {
        this.denyExit = denyExit;
    }


    /**
     * If not null and is a valid team ID string, this region should disallow entry to
     * teams other than the one specified.
     * @return team ID string or null
     */
    public String getTeamRestricted() {
        return teamRestricted;
    }


    /**
     * Set the team that is allowed to enter this region, or null to disable this behavior
     * @param teamRestricted The ID string of a team
     */
    public void setTeamRestricted(String teamRestricted) {
        this.teamRestricted = teamRestricted;
    }


    /**
     * This region should print a message when a player enters it, if not null
     * @return null or message string
     */
    public String getEntryHail() {
        return entryHail;
    }


    /**
     * Sets the message that should be displayed when a player enters it,
     * or null to disable this behavior.
     * @param entryHail Message string or null
     */
    public void setEntryHail(String entryHail) {
        this.entryHail = entryHail;
    }


    /**
     * This region should print a message when a player exits it, if not null
     * @return null or message string
     */
    public String getExitHail() {
        return exitHail;
    }


    /**
     * Sets the message that should be displayed when a player exits it,
     * or null to disable this behavior.
     * @param exitHail Message string or null
     */
    public void setExitHail(String exitHail) {
        this.exitHail = exitHail;
    }


    /**
     * Get a String List of "tags" for this region. This can be used so
     * plugins can listen for events and take action if a tag is present.
     */
    public List<String> getTags() {
        return tags;
    }


    /**
     * Sets the region's tag list
     * @param tags New List to replace the existing tag List
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }


}
