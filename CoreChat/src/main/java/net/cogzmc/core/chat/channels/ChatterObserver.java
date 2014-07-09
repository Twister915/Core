package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.COfflinePlayer;

/**
 * Implement this interface and register as an observer on the {@link net.cogzmc.core.chat.channels.IChannelManager} instance provided by {@link net.cogzmc.core.chat.CoreChat}.
 */
public interface ChatterObserver {
    /**
     * Called when a message is sent by a {@link net.cogzmc.core.player.CPlayer} on a {@link net.cogzmc.core.chat.channels.Channel}
     * @param sender The {@link net.cogzmc.core.player.CPlayer} who sent the message.
     * @param channel The {@link net.cogzmc.core.chat.channels.Channel} on which the message was sent.
     * @param message The message that was sent by the {@code player} on the {@code channel}.
     */
    void onMessageSent(COfflinePlayer sender, Channel channel, String message);
}
