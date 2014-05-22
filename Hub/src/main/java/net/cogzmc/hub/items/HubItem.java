package net.cogzmc.hub.items;

import lombok.Getter;
import lombok.NonNull;
import net.cogzmc.hub.Hub;
import net.cogzmc.hub.items.annotations.HubItemMeta;
import net.communitycraft.core.modular.ModularPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
public abstract class HubItem implements Listener {
    protected abstract void onLeftClick(Player player);
    protected abstract void onRightClick(Player player);
    protected abstract ItemStack getItemStack();

    @Getter private ModularPlugin instance;
    @Getter private HubItemMeta meta;

    public HubItem(ModularPlugin plugin, boolean requiresEvents) {
        this.instance = plugin;
        this.meta = getClass().getAnnotation(HubItemMeta.class);
        if (this.meta == null) return;
        if (requiresEvents) {
            Hub.getInstance().registerListener(this);
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public final void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if (event.getAction() == Action.PHYSICAL ||
                itemStack == null ||
                itemStack.getType() == Material.AIR ||
                !itemStack.hasItemMeta() ||
                !itemStack.getItemMeta().hasDisplayName() ||
                !itemStack.getItemMeta().getDisplayName().equals(getItemStack().getItemMeta().getDisplayName()))
            return;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                onRightClick(event.getPlayer());
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                onLeftClick(event.getPlayer());
                break;
            default:
                return;
        }
        event.setCancelled(true);
    }

    public final boolean shouldAdd(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack i : inventory.getContents()) {
            if (player.getInventory().contains(i)) return false;
        }
        return getItemStack() != null &&
                (this.meta.permission().isEmpty() || player.hasPermission(this.meta.permission())) &&
                !player.getInventory().contains(getItemStack()) &&
                getPropertyByType("enabled", Boolean.class);
    }

    public final <T> T getPropertyByType(@NonNull String property, @NonNull Class<T> classType) {
        //noinspection unchecked
        return (T) instance.getConfig().get("hub-items." + meta.key() + ".properties." + property);
    }

    public final String getProperty(@NonNull String property) {
        return getProperty(property, false, null);
    }

    public final String getProperty(@NonNull String property, @NonNull boolean prefix) {
        return getProperty(property, prefix, null);
    }

    public final String getProperty(@NonNull String property, @NonNull boolean prefix, String[]... replacements) {
        return instance.getFormat("hub-items." + meta.key() + ".properties." + property, prefix, replacements);
    }

    public final ConfigurationSection getConfigurationSection() {
        return instance.getConfig().getConfigurationSection("hub-items." + meta.key() + ".properties");
    }
}
