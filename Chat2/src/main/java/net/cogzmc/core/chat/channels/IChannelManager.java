package net.cogzmc.core.chat.channels;

import com.google.common.collect.ImmutableList;
import net.cogzmc.core.player.CPlayer;

public interface IChannelManager {
    ImmutableList<Channel> getChannels();
    Channel getChannelByName(String name);
    void registerChannel(Channel channel);
    void makePlayerParticipant(CPlayer player, Channel channel) throws ChannelException;
    void makePlayerListener(CPlayer player, Channel channel);
    void removePlayerAsListener(CPlayer player, Channel channel);
    Channel getChannelPlayerParticipatingIn(CPlayer player);
    ImmutableList<CPlayer> getParticipants(Channel channel);
    ImmutableList<CPlayer> getListeners(Channel channel);
    Channel getDefaultChannel();
    void reload();
    void save();
    boolean isParticipating(CPlayer player, Channel channel);
}
