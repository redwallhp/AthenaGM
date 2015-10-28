package io.github.redwallhp.athenagm;

import io.github.redwallhp.athenagm.arenas.ArenaHandler;
import io.github.redwallhp.athenagm.commands.ArenaCommands;
import io.github.redwallhp.athenagm.configuration.Configuration;
import io.github.redwallhp.athenagm.maps.VoidGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class AthenaGM extends JavaPlugin {


    public Configuration config;
    public ArenaHandler arenaHandler;


    @Override
    public void onEnable() {
        this.config = new Configuration(this);
        setupDirectories();
        registerCommands();
        this.arenaHandler = new ArenaHandler(this);
    }


    private void registerCommands() {
        ArenaCommands arenaCommands = new ArenaCommands(this);
    }


    private void setupDirectories() {
        // Set up directory structure if nonexistant
        // This is where a default maps would be copied over
        getMapsDirectory().mkdir();
        getMatchesDirectory().mkdir();
    }


    public File getMapsDirectory() {
        return new File("maps");
    }


    public File getMatchesDirectory() {
        return new File("matches");
    }


    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }


}
