package io.github.redwallhp.athenagm;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.github.redwallhp.athenagm.arenas.ArenaHandler;
import io.github.redwallhp.athenagm.commands.AdminCommands;
import io.github.redwallhp.athenagm.commands.ArenaCommands;
import io.github.redwallhp.athenagm.commands.MatchCommands;
import io.github.redwallhp.athenagm.configuration.Configuration;
import io.github.redwallhp.athenagm.hub.Hub;
import io.github.redwallhp.athenagm.maps.VoidGenerator;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.modules.ModuleLoader;
import io.github.redwallhp.athenagm.regions.RegionHandler;
import io.github.redwallhp.athenagm.tracker.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


/**
 * Project copyright 2015 redwall_hp
 * Licensed under the Lesser GNU Public License
 * http://www.gnu.org/licenses/lgpl-3.0.en.html
 * http://github.com/redwallhp
 */
public class AthenaGM extends JavaPlugin {


    public Configuration config;
    private ArenaHandler arenaHandler;
    private ModuleLoader moduleLoader;
    private RegionHandler regionHandler;
    private Hub hub;


    @Override
    public void onEnable() {
        this.config = new Configuration(this);
        setupDirectories();
        this.moduleLoader = new ModuleLoader(this);
        this.regionHandler = new RegionHandler(this);
        this.arenaHandler = new ArenaHandler(this);
        this.hub = new Hub(this);
        new Tracker(this);
        registerCommands();
    }


    @Override
    public void onDisable() {
        this.moduleLoader.unload();
    }


    private void registerCommands() {
        AdminCommands adminCommands = new AdminCommands(this);
        ArenaCommands arenaCommands = new ArenaCommands(this);
        MatchCommands matchCommands = new MatchCommands(this);
    }


    private void setupDirectories() {
        getMapsDirectory().mkdir();
        getMatchesDirectory().mkdir();
    }


    /**
     * Exposes the plugin's void world generator for Spigot and other plugins to use
     */
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }


    /**
     * Returns a File object representing the directory maps are stored in
     */
    public File getMapsDirectory() {
        return new File("maps");
    }


    /**
     * Returns a File object representing the directory where instanced maps for matches are stored
     */
    public File getMatchesDirectory() {
        return new File("matches");
    }


    /**
     * Get a File object representing the AthenaGM plugin directory.
     * Mostly a convenience method for other plugins.
     */
    public File getPluginDirectory() {
        return getDataFolder();
    }


    /**
     * Get a reference to the ArenaHandler, to look up active arenas or player-arena membership
     */
    public ArenaHandler getArenaHandler() {
        return arenaHandler;
    }


    /**
     * Get a reference to the RegionHandler
     */
    public RegionHandler getRegionHandler() {
        return regionHandler;
    }


    /**
     * Get a reference to a loaded Module
     * @param name The name of the Module, as returned by its getModuleName() method
     * @return A Module object, which can be cast to the original class of the module to access its methods
     */
    public Module getModule(String name) {
        return this.moduleLoader.getModule(name);
    }


    /**
     * Get a reference to the Hub
     */
    public Hub getHub() {
        return hub;
    }


    /**
     * Get a reference to WorldEdit if it is present.
     * Use this to check for the plugin's presence before calling any methods from WorldEditUtil.
     */
    public WorldEditPlugin getWE() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null;
        }
        return (WorldEditPlugin) plugin;
    }


}
