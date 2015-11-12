package io.github.redwallhp.athenagm.maps;


/**
 * Data structure representing team configuration loaded from the map's metadata file.
 * Used by the Match object to create a Team object.
 */
public class MapInfoTeam {

    private String id;
    private String color;
    private String kit;
    private Integer size;

    /**
     * Constructor
     * @param id String identifier of the team. e.g. "red"
     * @param color String representation of a ChatColor object for the team color
     * @param kit String identifier of the kit that this team's players should receive on respawn
     * @param size Maximum number of players that can join this team
     */
    public MapInfoTeam(String id, String color, String kit, Integer size) {
        this.id = id;
        this.color = color;
        this.kit = kit;
        this.size = size;
    }

    /**
     * String identifier of the team. e.g. "red"
     */
    public String getId() {
        return id;
    }

    /**
     * String representation of a ChatColor object for the team color
     */
    public String getColor() {
        return color;
    }

    /**
     * String identifier of the kit that this team's players should receive on respawn
     */
    public String getKit() {
        return kit;
    }

    /**
     * Maximum number of players that can join this team
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Returns true if all of the required fields are present
     */
    public boolean isValidTeam() {
        return (id != null && color != null && kit != null && size != null);
    }

}
