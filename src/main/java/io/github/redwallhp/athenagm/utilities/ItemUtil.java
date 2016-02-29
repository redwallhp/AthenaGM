package io.github.redwallhp.athenagm.utilities;

import org.bukkit.Color;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {


    /**
     * A list of Materials that do something when right-clicked.
     * e.g. for blacklisting in PlayerInteractEvent in spectator mode.
     */
    public static List<Material> getInteractiveBlocks() {
        List<Material> interactive = new ArrayList<Material>();
        interactive.add(Material.ACACIA_DOOR);
        interactive.add(Material.ACACIA_FENCE_GATE);
        interactive.add(Material.ANVIL);
        interactive.add(Material.ARMOR_STAND);
        interactive.add(Material.BEACON);
        interactive.add(Material.BED_BLOCK);
        interactive.add(Material.BIRCH_DOOR);
        interactive.add(Material.BIRCH_FENCE_GATE);
        interactive.add(Material.BREWING_STAND);
        interactive.add(Material.BURNING_FURNACE);
        interactive.add(Material.CAKE_BLOCK);
        interactive.add(Material.CHEST);
        interactive.add(Material.DARK_OAK_DOOR);
        interactive.add(Material.DARK_OAK_FENCE_GATE);
        interactive.add(Material.DAYLIGHT_DETECTOR);
        interactive.add(Material.DAYLIGHT_DETECTOR_INVERTED);
        interactive.add(Material.DIODE_BLOCK_OFF);
        interactive.add(Material.DIODE_BLOCK_ON);
        interactive.add(Material.DISPENSER);
        interactive.add(Material.DROPPER);
        interactive.add(Material.ENCHANTMENT_TABLE);
        interactive.add(Material.ENDER_CHEST);
        interactive.add(Material.FENCE_GATE);
        interactive.add(Material.FURNACE);
        interactive.add(Material.HOPPER);
        interactive.add(Material.ITEM_FRAME);
        interactive.add(Material.IRON_TRAPDOOR);
        interactive.add(Material.JUKEBOX);
        interactive.add(Material.JUNGLE_DOOR);
        interactive.add(Material.JUNGLE_FENCE_GATE);
        interactive.add(Material.LEVER);
        interactive.add(Material.NOTE_BLOCK);
        interactive.add(Material.REDSTONE_COMPARATOR_OFF);
        interactive.add(Material.REDSTONE_COMPARATOR_ON);
        interactive.add(Material.SPRUCE_DOOR);
        interactive.add(Material.SPRUCE_FENCE_GATE);
        interactive.add(Material.STONE_BUTTON);
        interactive.add(Material.TRAP_DOOR);
        interactive.add(Material.TRAPPED_CHEST);
        interactive.add(Material.WOOD_BUTTON);
        interactive.add(Material.WOODEN_DOOR);
        interactive.add(Material.WORKBENCH);
        return interactive;
    }


    /**
     * Returns limited Color objects (e.g. for team-colored armor) based
     * on a string representation of a ChatColor returned from the Team object.
     * @param color String representation of a ChatColor object (as returned from Team object)
     * @return Color object
     */
    public static Color getColorFormString(String color) {
        if (color.equalsIgnoreCase("red")) {
            return Color.RED;
        }
        else if (color.equalsIgnoreCase("blue")) {
            return Color.BLUE;
        }
        else if (color.equalsIgnoreCase("green")) {
            return Color.GREEN;
        }
        else if (color.equalsIgnoreCase("aqua")) {
            return Color.AQUA;
        }
        else if (color.equalsIgnoreCase("dark_aqua")) {
            return Color.fromRGB(0, 42, 42);
        }
        else if (color.equalsIgnoreCase("dark_blue")) {
            return Color.fromRGB(0, 0, 42);
        }
        else if (color.equalsIgnoreCase("dark_gray")) {
            return Color.fromRGB(21, 21, 21);
        }
        else if (color.equalsIgnoreCase("dark_green")) {
            return Color.fromRGB(0, 42, 0);
        }
        else if (color.equalsIgnoreCase("dark_purple")) {
            return Color.PURPLE;
        }
        else if (color.equalsIgnoreCase("dark_red")) {
            return Color.fromRGB(42, 0, 0);
        }
        else if (color.equalsIgnoreCase("light_purple")) {
            return Color.fromRGB(63, 21, 63);
        }
        else if (color.equalsIgnoreCase("gold")) {
            return Color.ORANGE;
        }
        else if (color.equalsIgnoreCase("black")) {
            return Color.BLACK;
        }
        else if (color.equalsIgnoreCase("white")) {
            return Color.WHITE;
        }
        else if (color.equalsIgnoreCase("yellow")) {
            return Color.YELLOW;
        }
        else {
            return Color.GRAY;
        }
    }


}
