package net.cogzmc.core.gui;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import net.cogzmc.core.effect.npc.ClickAction;
import net.cogzmc.core.modular.command.EmptyHandlerException;
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
    private static final Integer LORE_LINE_LENGTH = 30;

    @Setter(AccessLevel.PROTECTED) @NonNull private ItemStack stack;

    protected InventoryButton(ItemStack stack) {
        this.stack = stack;
    }

    protected InventoryButton(Material material, String title, String lore) {
        setItemStackUsing(material, 1, title, lore);
    }

    protected final void setItemStackUsing(Material material, Integer quantity, String title, String lore) {
        stack = new ItemStack(material, quantity);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(wrapLoreText(lore));
    }

    public static List<String> wrapLoreText(String string) {
        String workingString = ChatColor.translateAlternateColorCodes('&', string).trim();
        if (workingString.length() <= LORE_LINE_LENGTH) return Arrays.asList(workingString); //Because this is faster!
        double numberOfLines = Math.ceil(workingString.length() / LORE_LINE_LENGTH); //Always round up
        List<String> lines = new ArrayList<>(); //Get a list to put the lines in and fill it up
        String lastColor = null; //MUST start next line with last color of former line.
        for (int lineIndex = 0; lineIndex < numberOfLines; lineIndex++) {
            String line = workingString.substring(lineIndex * LORE_LINE_LENGTH, Math.min((lineIndex + 1) * LORE_LINE_LENGTH, workingString.length()));
            if (lastColor != null) line = lastColor + line;
            lastColor = ChatColor.getLastColors(line);
            lines.add(line);
        }
        return lines;
    }

    protected void onPlayerClick(CPlayer player, ClickAction action) throws EmptyHandlerException { throw new EmptyHandlerException(); }
}
