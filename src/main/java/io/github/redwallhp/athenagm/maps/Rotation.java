package io.github.redwallhp.athenagm.maps;

import io.github.redwallhp.athenagm.AthenaGM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rotation {


    private AthenaGM plugin;
    private List<String> mapList;
    private List<GameMap> maps;
    private int mapIndex;


    public Rotation(AthenaGM plugin, List<String> mapList) {
        this.plugin = plugin;
        this.mapList = mapList;
        this.mapIndex = 0;
        loadMaps();
    }


    private void loadMaps() {
        this.maps = new ArrayList<GameMap>();
        for (String mapName : mapList) {
            plugin.getLogger().info("Attempting to load map: " + mapName);
            File dir = new File(plugin.getMapsDirectory(), mapName);
            if (!dir.exists()) {
                plugin.getLogger().warning(String.format("Map '%s' does not exist. Skipping.", mapName));
                continue;
            }
            try {
                GameMap map = new GameMap(dir);
                this.maps.add(map);
                plugin.getLogger().info("Loaded map: " + map.getName());
            } catch (Exception ex) {
                plugin.getLogger().warning(String.format("Error parsing maps info for '%s'. Skipping.", mapName));
                if (plugin.config.DEBUG) ex.printStackTrace();
            }
        }
    }


    public List<GameMap> getMaps() {
        return maps;
    }


    public GameMap getCurrentMap() {
        try {
            return maps.get(mapIndex - 1);
        } catch (IndexOutOfBoundsException e) {
            return maps.get(0);
        }
    }


    public int advance() {
        mapIndex++;
        if (mapIndex > maps.size() - 1) mapIndex = 0;
        return mapIndex;
    }


    public GameMap getNextMap() {
        return maps.get(mapIndex);
    }


    public GameMap getMapByName(String name) {
        for (GameMap map : maps) {
            if (map.getName().toLowerCase().equals(name.toLowerCase())) {
                return map;
            }
        }
        return null;
    }


}
