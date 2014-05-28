package net.cogzmc.core.chat.channels;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.NonNull;
import net.cogzmc.core.player.CPlayer;

import java.util.List;
import java.util.Map;

public class ChannelManager implements IChannelManager {
    private Map<String, Channel> channels;
    @Getter private Channel defaultChannel;
    private Map<Channel, List<CPlayer>> listenerMap;
    private Map<CPlayer, Channel> activeChannels;

    @Override
    public ImmutableList<Channel> getChannels() {
        return ImmutableList.copyOf(channels.values());
    }

    @Override
    public Channel getChannelByName(String name) {
        return this.channels.get(name);
    }

    @Override
    public void makePlayerParticipant(@NonNull CPlayer player, @NonNull Channel channel) throws ChannelException {
        if (!channel.canBecomeParticipant(player)) throw new ChannelException("You cannot become a participant of this channel!");
        if (isParticipating(player, channel)) throw new ChannelException("You are already a participant of this channel!");
        Channel oldChannel = getChannelPlayerParticipatingIn(player);
        if (!oldChannel.canRemoveParticipant(player)) throw new ChannelException("You cannot leave this channel!");
        if (!channel.equals(defaultChannel)) activeChannels.put(player, channel);
        else activeChannels.remove(player);
    }

    @Override
    public void makePlayerListener(@NonNull CPlayer player, @NonNull Channel channel) {
        this.listenerMap
    }

    @Override
    public void removePlayerAsListener(@NonNull CPlayer player, @NonNull Channel channel) {

    }

    @Override
    public Channel getChannelPlayerParticipatingIn(@NonNull CPlayer player) {
        return this.activeChannels.containsKey(player) ? this.activeChannels.get(player) : defaultChannel;
    }

    @Override
    public ImmutableList<CPlayer> getParticipants(@NonNull Channel channel) {

    }

    @Override
    public ImmutableList<CPlayer> getListeners(@NonNull Channel channel) {
        return null;
    }

    @Override
    public void reload() {
        /*
         * TODO
         *  - Get channels from the database
         *  - Get default channel from those
         *  - Get
         */
    }

    @Override
    public void save() {

    }

    @Override
    public void registerChannel(@NonNull Channel channel) {
        if (this.channels.containsValue(channel)) throw new IllegalStateException("You cannot register the same channel twice!");
        this.channels.put(channel.getName(), channel);
    }

    @Override
    public boolean isParticipating(@NonNull CPlayer player, @NonNull Channel channel) {
        /*
         * If we don't have them in the active channels map, they must be in the default channel, and as such they're only a participant if the channel being test is default
         * Otherwise, they're in a specific channel, which we need to do an equals comparison on.
         */
        return !this.activeChannels.containsKey(player) ? channel.equals(defaultChannel) : this.activeChannels.get(player).equals(channel);
    }
}
