package net.cogzmc.core.gui;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class InventoryButton {
    private static final Integer lineLength = 30;

    @Setter(AccessLevel.PACKAGE) @NonNull private ItemStack stack = new ItemStack(Material.PAPER);

    protected void setItemStackUsing(Material material, Integer quantity, String title, String lore) {
        stack = new ItemStack(material, quantity);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(wrapLoreText(lore));
    }

    public static List<String> wrapLoreText(String string) {
        String workingString = ChatColor.translateAlternateColorCodes('&', string).trim();
        if (workingString.length() <= lineLength) return Arrays.asList(workingString); //Because this is faster!
        double numberOfLines = Math.ceil(workingString.length() / lineLength);
        List<String> lines = new ArrayList<>();
        String lastColor = null;
        for (int lineIndex = 0; lineIndex < numberOfLines; lineIndex++) {
            String line = workingString.substring(lineIndex * lineLength, Math.min((lineIndex + 1) * lineLength, workingString.length()));
            if (lastColor != null) line = lastColor + line;
            lastColor = ChatColor.getLastColors(line);
            lines.add(line);
        }
        return lines;
    }

    protected void onPlayerClick(CPlayer player) {}
}
