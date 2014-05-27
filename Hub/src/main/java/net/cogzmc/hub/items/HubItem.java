package net.cogzmc.hub.items;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.hub.Hub;
import net.cogzmc.hub.items.annotations.HubItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 * The {@link net.cogzmc.hub.items.HubItem} class is an effort to make the creation of hub items
 * simpler using the API. The abstract methods are called in the sub class, which also handles
 * any custom actions that are the item does.
 */
public abstract class HubItem implements Listener {
    /**
     * This method is called when the {@link org.bukkit.entity.Player} left clicks on an {@link org.bukkit.inventory.ItemStack}
     * with the correct {@link net.cogzmc.hub.items.annotations.HubItemMeta}.
     *
     * @param player The {@link org.bukkit.entity.Player} that clicked the item
     */
    protected abstract void onLeftClick(Player player);

    /**
     * This method is called when the {@link org.bukkit.entity.Player} right clicks on an {@link org.bukkit.inventory.ItemStack}
     * with the correct {@link net.cogzmc.hub.items.annotations.HubItemMeta}.
     *
     * @param player The {@link org.bukkit.entity.Player} that clicked the item
     */
    protected abstract void onRightClick(Player player);

    /**
     * Returns a list of {@link org.bukkit.inventory.ItemStack}s to be used for the {@link net.cogzmc.hub.items.HubItemsManager}.
     * The first item in the list is checked against the current inventory to see if it should be added to the {@link org.bukkit.entity.Player}'s
     * inventory.
     *
     * @return a list of {@link org.bukkit.inventory.ItemStack}
     */
    protected abstract List<ItemStack> getItemStacks();

    /**
     * The instance of the {@link net.cogzmc.core.modular.ModularPlugin} that registered this item.
     */
    @Getter private ModularPlugin instance;
    /**
     * The cached {@link net.cogzmc.hub.items.annotations.HubItemMeta} to retrieve metadata about the {@link net.cogzmc.hub.items.HubItem}
     */
    @Getter private HubItemMeta meta;

    @SneakyThrows
    public HubItem(ModularPlugin plugin, boolean requiresEvents) {
        this.instance = plugin;
        this.meta = getClass().getAnnotation(HubItemMeta.class);
        if (this.meta == null) throw new IllegalStateException("The HubItem class must be annotated with the HubItemMeta annotation.");
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
                !itemStack.getItemMeta().getDisplayName().equals(getItemStacks().get(0).getItemMeta().getDisplayName())) {
            return;
        }
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

    /**
     * Returns whether or not this {@link net.cogzmc.hub.items.HubItem} should be added to the {@link org.bukkit.entity.Player}'s inventory
     * based on permissions, inventory contents, and whether or not the item is enabled in the configuration.
     *
     * @param player {@link org.bukkit.entity.Player} to check for
     * @return whether or not the item should be added
     */
    public final boolean shouldAdd(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack i : inventory.getContents()) {
            if (player.getInventory().contains(i)) return false;
        }
        return
                (this.meta.permission().isEmpty() || player.hasPermission(this.meta.permission())) &&
                        !player.getInventory().contains(getItemStacks().get(0)) &&
                        isEnabled();
    }

    /**
     * Returns a raw object based on the classType parameter.
     *
     * @param property  the property key
     * @param classType the class to return the property as
     * @return the casted property
     */
    public final <T> T getPropertyByType(@NonNull String property, @NonNull Class<T> classType) {
        //noinspection unchecked
        return (T) instance.getConfig().get("hub-items." + meta.key() + ".properties." + property);
    }

    public boolean isEnabled() {
        return instance.getConfig().getBoolean("hub-items." + meta.key() + ".isEnabled");
    }

    /**
     * Returns a raw property from the config file
     *
     * @param property the property key
     * @return the formatted property
     */
    public final String getProperty(@NonNull String property) {
        return getProperty(property, false, null);
    }

    /**
     * Returns a formatted string with the changes specified by the parameters.
     *
     * @param property the property key
     * @param prefix   whether or not to attach the server prefix
     * @return the formatted property
     */
    public final String getProperty(@NonNull String property, @NonNull boolean prefix) {
        return getProperty(property, prefix, null);
    }

    /**
     * Returns a formatted string with the changes specified by the parameters.
     *
     * @param property     the property key
     * @param prefix       whether or not to attach the server prefix
     * @param replacements a list of replacements for the string
     * @return the formatted property
     */
    public final String getProperty(@NonNull String property, @NonNull boolean prefix, String[]... replacements) {
        return getFormat("hub-items." + meta.key() + ".properties." + property, prefix, replacements);
    }

    public final String getFormatRaw(String key, String[]... formatters) {
        FileConfiguration config = instance.getConfig();
        if (!config.contains(key)) return null; //Check if it has this format key, and if not return null
        String unFormattedString = ChatColor.translateAlternateColorCodes('&', config.getString(key)); //Get the un-formatted key
        if (formatters == null) return unFormattedString;
        for (String[] formatter : formatters) { //Iterate through the formatters
            if (formatter.length < 2) continue; //Validate the length
            unFormattedString = unFormattedString.replace(formatter[0], formatter[1]); //Replace all in the unformatted string
        }
        return unFormattedString; //Return
    }

    public final String getFormat(String key, boolean prefix, String[]... formatters) {
        String formatRaw = getFormatRaw(key, formatters);
        String prefix1 = instance.getFormatRaw("prefix");
        return !prefix || prefix1 == null ? formatRaw : prefix1 + formatRaw;
    }

    /**
     * Returns the {@link org.bukkit.configuration.ConfigurationSection} that data about this item is stored in.
     *
     * @return
     */
    public final ConfigurationSection getConfigurationSection() {
        return instance.getConfig().getConfigurationSection("hub-items." + meta.key() + ".properties");
    }
}
