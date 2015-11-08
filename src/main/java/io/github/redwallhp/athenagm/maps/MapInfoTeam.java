package io.github.redwallhp.athenagm.maps;


public class MapInfoTeam {

    private String id;
    private String color;
    private String kit;
    private Integer size;

    public MapInfoTeam(String id, String color, String kit, Integer size) {
        this.id = id;
        this.color = color;
        this.kit = kit;
        this.size = size;
    }

    public String getId() {
        return id;
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
        return (id != null && color != null && kit != null && size != null);
    }

}
