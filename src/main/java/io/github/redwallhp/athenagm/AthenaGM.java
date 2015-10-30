package io.github.redwallhp.athenagm;

import io.github.redwallhp.athenagm.arenas.ArenaHandler;
import io.github.redwallhp.athenagm.commands.ArenaCommands;
import io.github.redwallhp.athenagm.configuration.Configuration;
import io.github.redwallhp.athenagm.maps.VoidGenerator;
import io.github.redwallhp.athenagm.modules.Module;
import io.github.redwallhp.athenagm.modules.ModuleLoader;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class AthenaGM extends JavaPlugin {


    public Configuration config;
    public ArenaHandler arenaHandler;
    private ModuleLoader moduleLoader;


    @Override
    public void onEnable() {
        this.config = new Configuration(this);
        setupDirectories();
        this.arenaHandler = new ArenaHandler(this);
        this.moduleLoader = new ModuleLoader(this);
        registerCommands();
    }


    @Override
    public void onDisable() {
        this.moduleLoader.unload();
    }


    private void registerCommands() {
        ArenaCommands arenaCommands = new ArenaCommands(this);
    }


    private void setupDirectories() {
        getMapsDirectory().mkdir();
        getMatchesDirectory().mkdir();
    }


    public File getMapsDirectory() {
        return new File("maps");
    }


    public File getMatchesDirectory() {
        return new File("matches");
    }


    public Module getModule(String name) {
        return this.moduleLoader.getModule(name);
    }


    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }


}
