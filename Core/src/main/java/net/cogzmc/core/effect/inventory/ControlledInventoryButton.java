package net.cogzmc.core.effect.inventory;

import net.cogzmc.core.player.CPlayer;
import org.bukkit.inventory.ItemStack;

public abstract class ControlledInventoryButton {
    protected void onUse(CPlayer player) {}
    protected abstract ItemStack getStack(CPlayer player);
}
