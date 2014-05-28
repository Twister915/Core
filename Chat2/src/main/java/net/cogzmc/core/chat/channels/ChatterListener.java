package net.cogzmc.core.chat.channels;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.player.COfflinePlayer;
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
        try {
            attemptCrossServer(sender, message, channel);
        } catch (ChannelException ignored) {
        }
    }

    public static void sendMessageOnChannel(CPlayer sender, String message, Channel channel) throws ChannelException {
        IChannelManager cManager = CoreChat.getInstance().getChannelManager();
        if (!cManager.isListening(sender, channel)
                || !channel.canBecomeParticipant(sender))
            throw new ChannelException("You cannot chat in this channel");
        attemptCrossServer(sender, message, channel);
    }

    private static void attemptCrossServer(CPlayer player, String message, Channel channel) throws ChannelException {
        if (!channel.isCrossServer() || Core.getNetworkManager() == null) return;
        ChatNetCommand chatNetCommand = new ChatNetCommand(message, channel.getName(), player.getUniqueIdentifier().toString());
        Core.getNetworkManager().sendMassNetCommand(chatNetCommand);
    }

    static void handleCrossServer(COfflinePlayer sender, String message, Channel channel) throws ChannelException {
        IChannelManager cManager = CoreChat.getInstance().getChannelManager();
        String formatMessage = channel.formatMessage(sender, message);
        String s = channel.formatMessage(sender, message);
        for (CPlayer cPlayer : cManager.getListeners(channel)) {
            cPlayer.sendMessage(s);
        }
        for (ChatterObserver chatterObserver : cManager.getChatterObservers()) {
            chatterObserver.onMessageSent(sender, channel, message);
        }
    }
}
