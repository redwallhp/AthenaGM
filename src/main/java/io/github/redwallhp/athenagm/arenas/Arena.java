package io.github.redwallhp.athenagm.arenas;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.configuration.ConfiguredArena;
import io.github.redwallhp.athenagm.events.MatchCreateEvent;
import io.github.redwallhp.athenagm.maps.MapLoader;
import io.github.redwallhp.athenagm.maps.Rotation;
import io.github.redwallhp.athenagm.matches.Match;
import io.github.redwallhp.athenagm.matches.MatchState;
import io.github.redwallhp.athenagm.matches.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class Arena {

    private ArenaHandler handler;
    private AthenaGM plugin;
    private String id;
    private String name;
    private String gameMode;
    private Integer timeLimit;
    private List<String> mapList;
    private Rotation rotation;
    private MapLoader mapLoader;
    private WeakReference<World> world;
    private File worldFile;
    private Match match;


    public Arena(ArenaHandler handler, ConfiguredArena configuredArena) {

        this.handler = handler;
        this.plugin = handler.getPluginInstance();
        this.id = configuredArena.getId();
        this.name = configuredArena.getName();
        this.gameMode = configuredArena.getGameMode();
        this.timeLimit = configuredArena.getTimeLimit();
        this.mapList = configuredArena.getMapList();

        this.rotation = new Rotation(plugin, this.mapList);
        this.mapLoader = new MapLoader(rotation.getCurrentMap(), this);

        Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
            public void run() {
                startNewMatch();
            }
        });

    }


    public void startNewMatch() {

        if (rotation.getNextMap().equals(mapLoader.getMap())) {
            rotation.advance();
        }

        World oldWorld = (this.world == null) ? null : getWorld();
        File oldFile = (this.worldFile == null) ? null : getWorldFile();

        mapLoader.load();
        plugin.getRegionHandler().loadRegions(world.get(), mapLoader.getMap());
        this.match = new Match(this, this.mapLoader.getUUID(), this.mapLoader.getMap());
        MatchCreateEvent event = new MatchCreateEvent(this.match);
        Bukkit.getPluginManager().callEvent(event);
        mapLoader = new MapLoader(rotation.getNextMap(), this);

        if (oldWorld != null) {
            bringPlayers(oldWorld, world.get());
            plugin.getRegionHandler().unloadRegions(oldWorld);
            Bukkit.unloadWorld(oldWorld, false);
            mapLoader.destroyWorldInstanceCopy(oldFile);
        }

    }


    private void bringPlayers(World oldWorld, World newWorld) {
        Location loc = new Location(newWorld, 0, 70, 0);
        for (Player player : oldWorld.getPlayers()) {
            player.teleport(loc);
        }
    }


    public boolean forceMapChange(String fileName) {
        boolean success = rotation.setNextMap(fileName);
        if (success) {
            mapLoader = new MapLoader(rotation.getNextMap(), this);
            match.setState(MatchState.ENDED); //force end without starting next-round timer
            startNewMatch();
            return true;
        }
        return false;
    }


    public ArenaHandler getHandler() {
        return this.handler;
    }

    public AthenaGM getPlugin() {
        return this.plugin;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public Integer getMaxPlayers() {
        int maxPlayers = 0;
        for (Team team : getMatch().getTeams().values()) {
            if (team.isSpectator()) continue;
            maxPlayers = maxPlayers + team.getSize();
        }
        return maxPlayers;
    }

    public Integer getPlayerCount() {
        return getMatch().getTotalPlayers();
    }

    public Integer getTimeLimit() {
        return this.timeLimit;
    }

    public List<String> getMapList() {
        return this.mapList;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public World getWorld() {
        return this.world.get();
    }

    public void setWorld(World world) {
        this.world = new WeakReference<World>(world);
    }

    public File getWorldFile() {
        return this.worldFile;
    }

    public void setWorldFile(File file) {
        this.worldFile = file;
    }

    public Match getMatch() {
        return this.match;
    }


}
