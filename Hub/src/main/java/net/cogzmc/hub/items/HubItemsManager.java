package net.cogzmc.hub.items;

import net.cogzmc.hub.Hub;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the adding of {@link net.cogzmc.hub.items.HubItem}s to the {@link org.bukkit.entity.Player}'s
 * inventory during the {@link #onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent)}. Hub items should also be registered
 * through this class, which are registered in the {@link #hubItems} {@link java.util.List}.
 */
public final class HubItemsManager implements Listener {
    private final List<HubItem> hubItems;

    public HubItemsManager() {
        this.hubItems = new ArrayList<>();
    }

    public final void registerHubItem(HubItem hubItem) {
        this.hubItems.add(hubItem);
        Hub.getInstance().logMessage(ChatColor.RED + "Registered the HubItem with the key: " + hubItem.getMeta().key());
    }

    public final void unregisterHubItem(HubItem hubItem) {
        this.hubItems.remove(hubItem);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (HubItem item : this.hubItems) {
            if (!item.shouldAdd(player)) continue;
            ItemStack itemStack = item.getItemStacks().get(0);
            if (item.getMeta().slot() == -1) {
                player.getInventory().addItem(itemStack);
                player.updateInventory();
                continue;
            }
            player.getInventory().setItem(item.getMeta().slot(), itemStack);
            player.updateInventory();
        }
    }
}
