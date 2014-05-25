package net.cogzmc.hub.items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class HubItemsManager implements Listener {
    private final List<HubItem> hubItems;

    public HubItemsManager() {
        this.hubItems = new ArrayList<>();
    }

    public final void registerHubItem(HubItem hubItem) {
        this.hubItems.add(hubItem);
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
