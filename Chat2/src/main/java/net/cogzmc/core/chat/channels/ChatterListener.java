package net.cogzmc.core.chat.channels;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

@Data
public final class ChatterListener implements Listener {
    private final IChannelManager channelManager;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        CPlayer player = Core.getOnlinePlayer(event.getPlayer());
        Channel activeChannel = channelManager.getChannelPlayerParticipatingIn(player);
        List<Player> recipients = new ArrayList<>();
        for (CPlayer cPlayer : channelManager.getListeners(activeChannel)) {
            recipients.add(cPlayer.getBukkitPlayer());
        }
        event.getRecipients().removeAll(event.getRecipients());
        event.getRecipients().addAll(recipients);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat0(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        CPlayer sender = Core.getOnlinePlayer(event.getPlayer());
        Channel channel = channelManager.getChannelPlayerParticipatingIn(sender);
        String message = channel.formatMessage(sender, event.getMessage());
        for (Player player : event.getRecipients()) {
            player.sendMessage(message);
        }
        for (ChatterObserver chatterObserver : channelManager.getChatterObservers()) {
            chatterObserver.onMessageSent(sender, channel, message);
        }
    }
}
