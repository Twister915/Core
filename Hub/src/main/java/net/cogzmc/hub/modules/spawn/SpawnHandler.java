package net.cogzmc.hub.modules.spawn;

import lombok.Getter;
import net.cogzmc.hub.Hub;
import net.cogzmc.hub.util.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class SpawnHandler {
    @Getter private Location spawn;

    public SpawnHandler() {
        updateLocalSpawn();
    }

    public final void setSpawn(Location location) {
        this.spawn = location;
        Hub.getInstance().getConfig().set("spawn", LocationUtils.encodeLocationString(location));
    }

    public final void sendToSpawn(Player player) {
        player.teleport(spawn);
        player.sendMessage(ChatColor.GREEN + "You have been teleported to spawn!");
    }

    private void updateLocalSpawn() {
        try {
            spawn = LocationUtils.parseLocationString(Hub.getInstance().getConfig().getString("spawn"));
        } catch (NullPointerException ex) {
            spawn = null;
            Hub.getInstance().logMessage("No spawn point set!");
        }
    }
}
