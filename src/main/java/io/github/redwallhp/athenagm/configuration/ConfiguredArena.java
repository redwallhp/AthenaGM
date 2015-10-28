package io.github.redwallhp.athenagm.configuration;

import java.util.List;

public class ConfiguredArena {

    private String id;
    private String name;
    private String gameMode;
    private Integer maxPlayers;
    private Integer timeLimit;
    private List<String> mapList;

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
