package io.github.redwallhp.athenagm.configuration;

import java.util.List;

/**
 * Data structure to hold the configuration data for arenas
 */
public class ConfiguredArena {

    private String id;
    private String name;
    private String gameMode;
    private Integer maxPlayers;
    private Integer timeLimit;
    private List<String> mapList;

    /**
     * Constructor
     * @param id String identifier of this arena
     * @param name Long name of the arena
     * @param gameMode Currently unused (?)
     * @param maxPlayers Maximum number of players that can join the arena. Currently unused (?)
     * @param timeLimit Time limit of rounds in this arena.
     * @param mapList String List of map filenames that will go in this arena's map rotation.
     */
    public ConfiguredArena(String id, String name, String gameMode, Integer maxPlayers, Integer timeLimit, List<String> mapList) {
        this.id = id;
        this.name = name;
        this.gameMode = gameMode;
        this.maxPlayers = maxPlayers;
        this.timeLimit = timeLimit;
        this.mapList = mapList;
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
        return this.maxPlayers;
    }

    public Integer getTimeLimit() {
        return this.timeLimit;
    }

    public List<String> getMapList() {
        return this.mapList;
    }

}
