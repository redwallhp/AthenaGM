package io.github.redwallhp.athenagm.maps;


public class MapInfoTeam {

    private String id;
    private String name;
    private String color;
    private String kit;
    private Integer size;

    public MapInfoTeam(String id, String name, String color, String kit, Integer size) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.kit = kit;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getKit() {
        return kit;
    }

    public Integer getSize() {
        return size;
    }

    public boolean isValidTeam() {
        return (id != null && name != null && color != null && kit != null && size != null);
    }

}
