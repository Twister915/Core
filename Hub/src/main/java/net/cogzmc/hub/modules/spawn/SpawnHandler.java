package net.cogzmc.hub.modules.spawn;

import lombok.Getter;
import net.cogzmc.core.util.LocationUtils;
import net.cogzmc.hub.Hub;
import net.cogzmc.hub.model.Setting;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class SpawnHandler implements Listener {
    @Getter private Location spawn;
    //private static final String CONFIG_KEY = "spawn";

    public SpawnHandler() {
        updateSpawn();
    }

    public final void setSpawn(Location location) {
        this.spawn = location;
        //Hub.getInstance().getConfig().set(CONFIG_KEY, LocationUtils.encodeLocationString(location));
        Hub.getInstance().getSettingsManager().setSettingValue(Setting.SPAWN, LocationUtils.encodeLocationString(location));
    }

    public final void sendToSpawn(Player player) {
        player.teleport(spawn == null ? player.getWorld().getSpawnLocation() : spawn);
        player.sendMessage(ChatColor.GREEN + "You have been teleported to spawn!");
    }

    public void updateSpawn() {
        try {
            spawn = LocationUtils.parseLocationString(Hub.getInstance().getSettingsManager().getSettingValueFor(Setting.SPAWN, String.class));
        } catch (NullPointerException ex) {
            spawn = null;
            Hub.getInstance().logMessage("No spawn point set!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.spawn == null) return;
        event.getPlayer().teleport(this.spawn);
    }
}
