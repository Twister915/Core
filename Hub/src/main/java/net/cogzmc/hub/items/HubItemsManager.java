package net.cogzmc.hub.items;

import net.communitycraft.core.player.CPlayer;
import net.communitycraft.core.player.CPlayerConnectionListener;
import net.communitycraft.core.player.CPlayerJoinException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.net.InetAddress;
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
public final class HubItemsManager implements CPlayerConnectionListener {
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

    @Override
    public final void onPlayerJoin(CPlayer cPlayer, InetAddress address) throws CPlayerJoinException {
        Player player = cPlayer.getBukkitPlayer();
        for (HubItem item : this.hubItems) {
            if (!item.shouldAdd(player)) continue;
            ItemStack itemStack = item.getItemStack();
            if (item.getMeta().slot() == -1) {
                player.getInventory().addItem(itemStack);
                player.updateInventory();
                continue;
            }

            player.getInventory().setItem(item.getMeta().slot(), itemStack);
            player.updateInventory();
        }
    }

    @Override
    public final void onPlayerDisconnect(CPlayer player) {

    }
}
