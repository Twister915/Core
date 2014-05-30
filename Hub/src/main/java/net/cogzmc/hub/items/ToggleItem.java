package net.cogzmc.hub.items;

import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/28/2014
 */
public abstract class ToggleItem extends HubItem implements CPlayerConnectionListener {
    private List<String> enabledFor = new ArrayList<>();

    public ToggleItem(ModularPlugin plugin) {
        super(plugin, true);
        plugin.getPlayerManager().registerCPlayerConnectionListener(this);
    }

    public abstract void handleToggle(Player player, boolean newState);

    @Override
    protected void onLeftClick(Player player) {
        handleToggle(player, !enabledFor.contains(player.getName()));
    }

    @Override
    protected void onRightClick(Player player) {
        onLeftClick(player);
    }

    public boolean isEnabled(Player player) {
        return this.enabledFor.contains(player.getName());
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        if (this.enabledFor.contains(player.getName())) this.enabledFor.remove(player.getName());
    }
}
