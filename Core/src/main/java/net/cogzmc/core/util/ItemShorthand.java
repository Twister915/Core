package net.cogzmc.core.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
public final class ItemShorthand {
    private final Material material;

    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private short dataValue;
    private int quantity;

    private ItemShorthand(Material m) {
        this.material = m;
    }

    public static ItemShorthand withMaterial(Material material) {
        return new ItemShorthand(material);
    }

    public ItemShorthand withName(String name) {
        this.name = name;
        return this;
    }

    public ItemShorthand withLore(String l) {
        checkLore();
        lore.add(l);
        return this;
    }

    public ItemShorthand withLore(Collection<String> l) {
        checkLore();
        lore.addAll(l);
        return this;
    }

    public ItemShorthand withEnchant(Enchantment enchant, int level) {
        enchantments.put(enchant, level);
        return this;
    }

    private void checkLore() {
        if (lore == null) lore = new ArrayList<>();
    }

    public ItemShorthand withDataValue(short dataValue) {
        this.dataValue = dataValue;
        return this;
    }

    public ItemShorthand withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public ItemStack get() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null) itemMeta.setDisplayName(name);
        if (lore != null) itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        if (quantity > 1) itemStack.setAmount(quantity);
        if (dataValue > 0) itemStack.setDurability(dataValue);
        itemStack.addEnchantments(enchantments);
        return itemStack;
    }
}
