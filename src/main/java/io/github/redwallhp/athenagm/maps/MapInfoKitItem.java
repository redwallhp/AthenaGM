package io.github.redwallhp.athenagm.maps;

import io.github.redwallhp.athenagm.utilities.ItemUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Data structure defining a player kit
 */
public class MapInfoKitItem {


    private ItemStack item;
    private String kit;
    private int inventorySlot;
    private boolean dropOnDeath;


    /**
     * Constructor
     * @param kit String identifier of the kit
     * @param inventorySlot The slot this should go into in the player's inventory
     * @param material Item type
     * @param quantity Quantity of the item
     */
    public MapInfoKitItem(String kit, int inventorySlot, String material, int quantity) {
        this.kit = kit;
        this.inventorySlot = inventorySlot;
        this.item = new ItemStack(Material.matchMaterial(material), quantity);
    }


    /**
     * Return the ItemStack
     */
    public ItemStack getItem() {
        return item;
    }


    /**
     * Return the inventory slot for the item
     */
    public int getInventorySlot() {
        return inventorySlot;
    }


    /**
     * Add a named enchantment to the ItemStack
     * @param name String identifier of the enchantment
     * @param level Level of the enchantment
     */
    public void addEnchantment(String name, int level) {
        Enchantment enchantment = Enchantment.getByName(name.toUpperCase());
        if (enchantment != null) {
            this.item.addEnchantment(enchantment, level);
        }
    }


    /**
     * Apply a color to leather armor
     * @param colorName String representation of a ChatColor
     */
    public void colorizeArmor(String colorName) {
        Color color = ItemUtil.getColorFormString(colorName);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
    }


    /**
     * Whether the item will be dropped or blacklisted on player death
     */
    public boolean dropOnDeath() {
        return dropOnDeath;
    }


    /**
     * Determine whether the item will be dropped or blacklisted on player death
     */
    public void setDropOnDeath(boolean drop) {
        dropOnDeath = drop;
    }


}
