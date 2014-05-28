package net.cogzmc.core.chat.channels;

import com.google.common.collect.ImmutableList;
import net.cogzmc.core.player.CPlayer;

public interface IChannelManager {
    ImmutableList<Channel> getChannels();
    Channel getChannelByName(String name);
    void registerChannel(Channel channel);
    void makePlayerParticipant(CPlayer player, Channel channel) throws ChannelException;
    void makePlayerListener(CPlayer player, Channel channel) throws ChannelException;
    void removePlayerAsListener(CPlayer player, Channel channel) throws ChannelException;
    Channel getChannelPlayerParticipatingIn(CPlayer player);
    ImmutableList<CPlayer> getParticipants(Channel channel);
    ImmutableList<CPlayer> getListeners(Channel channel);
    Channel getDefaultChannel();
    void reload();
    void save();
    boolean isParticipating(CPlayer player, Channel channel);
    boolean isListening(CPlayer player, Channel channel);

    ImmutableList<MessageProcessor> getMessageProcessors();
    ImmutableList<MessageArgumentDelegate> getMessageArgumentDelegates();

    void registerMessageProcessor(MessageProcessor messageProcessor);
    void registerMessageArgumentDelegate(MessageArgumentDelegate messageArgumentDelegate);
    void unregisterMessageProcessor(MessageProcessor messageProcessor);
    void unregisterMessageArgumentDelegate(MessageArgumentDelegate messageArgumentDelegate);

    void registerChannelManagerReloadObserver(ChannelManagerReloadObserver observer);
    void unregisterChannelManagerReloadObserver(ChannelManagerReloadObserver observer);
}
