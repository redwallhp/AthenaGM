package io.github.redwallhp.athenagm.utilities;


import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;


/**
 * WorldEdit functions, isolated in a container class so AthenaGM doesn't depend on WorldEdit.
 * Always use AthenaGM's getWE() method to check if WorldEdit is present before calling one of these
 * static methods.
 */
public class WorldEditUtil {


    /**
     * Set the player's WorldEdit selection
     * @param player The player
     * @param world The world the selection is in
     * @param min The first corner of the selection
     * @param max The second corner of the selection
     */
    public static void setPlayerSelection(Player player, World world, Vector min, Vector max) {
        if (getWE() != null) {
            CuboidSelection sel = new CuboidSelection(world, min.toLocation(world), max.toLocation(world));
            getWE().setSelection(player, sel);
        }
    }


    /**
     * Get a reference to WE. For use internal to this class only, otherwise it breaks the soft dependency.
     * Always check if WorldEdit is present using the method in the main plugin class.
     */
    private static WorldEditPlugin getWE() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null;
        }
        return (WorldEditPlugin) plugin;
    }


}
