package net.cogzmc.hub.modules;

import lombok.Getter;
import net.cogzmc.hub.Hub;
import net.communitycraft.core.modular.ModularPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * <p>
 * Latest Change: By Joey to make things a bit faster using private booleans.
 * <p>
 *
 * This internal module is intended
 *
 * @author Jake
 * @since 5/22/2014
 */
@Getter
public final class HideStream implements Listener {
    private boolean welcomeMessages; //Stores if we should display welcome messages
    private boolean hideStream; //Stores if we should hide the stream of players

    //Initializer so that we always reload the settings from the configuration.
    {
        reloadSettings(Hub.getInstance() );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onJoin(PlayerJoinEvent event) {
        Hub instance = Hub.getInstance();
        if (isHideStream()) event.setJoinMessage(null);
        else event.setJoinMessage(instance.getFormat("join-message", false, new String[]{"<player>", event.getPlayer().getName()}));
        if (isWelcomeMessages()) Bukkit.broadcastMessage(instance.getFormat("welcome-message", false, new String[]{"<player>", event.getPlayer().getName()}));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onQuit(PlayerQuitEvent event) {
        if (isHideStream()) event.setQuitMessage(null);
        //Gets the format from the formats.yml (quit-message) replacing <player> with the player's name.
        else event.setQuitMessage(Hub.getInstance().getFormat("quit-message", false, new String[]{"<player>", event.getPlayer().getName()}));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerKick(PlayerKickEvent event) {
        //Identical to the onQuit above
        if (isHideStream()) event.setLeaveMessage(null);
        //Gets the format from the formats.yml (quit-message) replacing <player> with the player's name.
        else event.setLeaveMessage(Hub.getInstance().getFormat("quit-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
    }

    public void reloadSettings(ModularPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        this.hideStream = config.getBoolean("hide-stream");
        this.welcomeMessages = config.getBoolean("welcome-messages");
    }
}
