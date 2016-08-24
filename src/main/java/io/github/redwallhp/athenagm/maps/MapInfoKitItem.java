package io.github.redwallhp.athenagm.maps;

import io.github.redwallhp.athenagm.utilities.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure defining a player kit
 */
public class MapInfoKitItem {


    private ItemStack item;
    private String kit;
    private int inventorySlot;
    private boolean dropOnDeath;
    private int chance;


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
        this.chance = 100;
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
            this.item.addUnsafeEnchantment(enchantment, level);
        }
    }


    /**
     * Turn an ItemStack with a Material of POTION into a functional potion
     * @param typeName PotionType string representing the potion type. (Matches Bukkit PotionType enum.)
     * @param upgraded Boolean specifying a level two potion if true
     * @param extended Is this an extended duration potion?
     */
    public void addPotion(String typeName, boolean upgraded, boolean extended) {
        Material mat = this.item.getType();
        List<Material> types = new ArrayList<Material>();
        types.add(Material.POTION);
        types.add(Material.SPLASH_POTION);
        types.add(Material.LINGERING_POTION);
        types.add(Material.TIPPED_ARROW);
        if (!types.contains(mat)) return;
        try {
            PotionType type = PotionType.valueOf(typeName.toUpperCase());
            PotionData pd = new PotionData(type, extended, upgraded);
            PotionMeta meta = (PotionMeta) this.item.getItemMeta();
            meta.setBasePotionData(pd);
            this.item.setItemMeta(meta);
        } catch (Exception ex) {
            Bukkit.getLogger().warning(String.format("Kit configuration error: %s", ex.getMessage()));
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
     * Set the item name
     * @param name Name to appear on the item. Accepts color codes with the '&' token.
     */
    public void setName(String name) {
        if (name.length() < 1) return;
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
    }


    public void setLore(String lore) {
        if (lore.length() < 1) return;
        lore = ChatColor.translateAlternateColorCodes('&', lore);
        List<String> lines = new ArrayList<String>(Arrays.asList(lore.split("\\|")));
        if (lines.size() > 0) {
            ItemMeta meta = this.item.getItemMeta();
            meta.setLore(lines);
            item.setItemMeta(meta);
        }
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


    /**
     * How often this item should be given to a player, expressed as a percentage of respawns.
     * e.g. a value of 100 means it will be given every time, while a value of 1 would be 1 in 100 times.
     */
    public int getChance() {
        return chance;
    }


    /**
     * Set how often this item should be added to the player's inventory on respawn, expressed as a percentage.
     * @param chance Percentage of respawns that this item should be given
     */
    public void setChance(int chance) {
        this.chance = chance;
    }


}
