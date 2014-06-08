package net.cogzmc.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe
 */
public class ItemMetaFactory {

    ItemStack k;
    ItemMeta m;
    List<String> lore;

    /**
     * Creates a new Meta Factory from the passed itemstack
     * @param k Itemstack to edit Meta of
     */

    public ItemMetaFactory(ItemStack k){
        this.k = k;
        if(k != null && k.getType() != Material.AIR) {
            this.m = k.getItemMeta();
            if (m == null) {
                m = Bukkit.getItemFactory().getItemMeta(k.getType());
            }
            if (m.hasLore()) {
                lore = m.getLore();
            } else {
                lore = new ArrayList<>();
            }
        }
        else
        {
            m = Bukkit.getItemFactory().getItemMeta(Material.GRASS);//Basic Item Meta, will be nulled since k is null
        }
    }


    /**
     * Inflicts Meta to itemstack.
     * MUST CALL TO INFLICT ANY CHANGES
     */
    public void set(){
        if(k != null) {
            m.setLore(lore);
            k.setItemMeta(m);
        }
    }

    /**
     * Sets the display name of the item
     * @param name New item name
     * @return Chainable Item meta factory
     */
    public ItemMetaFactory setDisplayName(String name){
        m.setDisplayName(name);
        return this;
    }

    /**
     * Sets the lore of the item
     * @param lore New Item Lore
     * @return Chainable Item Meta Factory
     */
    public ItemMetaFactory setLore(List<String> lore){
        m.setLore(lore);
        this.lore = lore;
        return this;
    }

    /**
     * Clears the lore to the default lore value for the itemstack
     * @return Chainable Item Meta Factory
     */
    public ItemMetaFactory clearLore(){
        lore = new ArrayList<>();
        return this;
    }

    /**
     * Appends the line to the end of the item lore
     * @param s Line to append
     * @return Chainable Item Meta Factory
     */
    public ItemMetaFactory addToLore(String s){
        lore.add(s);
        return this;
    }

    /**
     * Adds an enchantment to the itemstack
     * @param e Enchantment type to add
     * @param level Level of enchantment to add
     * @param ambient   Whether or not to make the enchantment ambient
     * @return  Chainable Item Meta Factory
     */
    public ItemMetaFactory addEnchantment(Enchantment e, Integer level, boolean ambient){
        m.addEnchant(e,level,ambient);
        return this;
    }

    /**
     * Removes all enchantments of the passed enchantment type
     * @param e Enchantment to remove
     * @return  Chainable Item Meta Factory
     */
    public ItemMetaFactory removeEnchantment(Enchantment e){
        m.removeEnchant(e);
        return this;
    }

    /**
     * Removes all enchantments from the item stack
     * @return  Chainable Item Meta Factory
     */
    public ItemMetaFactory stripEnchantments(){
        m.getEnchants().clear();
        return this;
    }

    /**
     * Resets the item stack to its default state
     * @return Chainable Item Meta Factory
     */
    public ItemMetaFactory clean(){
        m.setDisplayName(null);
        m.setLore(null);
        lore = new ArrayList<>();
        for(Enchantment e : m.getEnchants().keySet()){
            m.removeEnchant(e);
        }
        return this;
    }

    /**
     * Static constructor
     * @param k Item stack to create Factory for
     * @return  Chainable Item Meta Factory
     */
    public static ItemMetaFactory create(ItemStack k){
        return new ItemMetaFactory(k);
    }

}