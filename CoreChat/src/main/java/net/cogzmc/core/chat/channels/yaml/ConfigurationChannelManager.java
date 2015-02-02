package net.cogzmc.core.chat.channels.yaml;

import net.cogzmc.core.chat.channels.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigurationChannelManager implements IChannelManager, CPlayerConnectionListener {
    private final ConfigurationChannelSource configurationChannelSource;

    private final List<ChannelManagerReloadObserver> channelManagerReloadObservers = new ArrayList<>();

    private Map<String, Channel> channels;
    @Getter private Channel defaultChannel;
    private Map<Channel, List<CPlayer>> listenerMap;
    private Map<CPlayer, Channel> activeChannels;
    private List<MessageArgumentDelegate> messageArgumentDelegates;
    private List<MessageProcessor> messageProcessors;
    private List<ChatterObserver> chatterObservers;

    public ConfigurationChannelManager() throws ChannelException {
        this.configurationChannelSource = new ConfigurationChannelSource(this);
        reload();
    }

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
        if (!isListening(player, channel)) makePlayerListener(player, channel);
        Channel oldChannel = getChannelPlayerParticipatingIn(player);
        if (!oldChannel.canRemoveParticipant(player)) throw new ChannelException("You cannot leave this channel!");
        if (!channel.equals(defaultChannel)) activeChannels.put(player, channel);
        else activeChannels.remove(player);
    }

    @Override
    public void makePlayerListener(@NonNull CPlayer player, @NonNull Channel channel) throws ChannelException {
        if (isListening(player, channel)) throw new ChannelException("You are already a listener of this channel!");
        if (channel.equals(defaultChannel)) throw new ChannelException("You are always in this channel by default!");
        this.listenerMap.get(channel).add(player);
    }

    @Override
    public void removePlayerAsListener(@NonNull CPlayer player, @NonNull Channel channel) throws ChannelException {
        if (!isListening(player, channel)) throw new ChannelException("You are not listening to this channel!");
        if (channel.equals(defaultChannel)) throw new ChannelException("You cannot leave the default channel!");
        if (isParticipating(player, channel)) makePlayerParticipant(player, defaultChannel);
        this.listenerMap.get(channel).remove(player);
    }

    @Override
    public Channel getChannelPlayerParticipatingIn(@NonNull CPlayer player) {
        return this.activeChannels.containsKey(player) ? this.activeChannels.get(player) : defaultChannel;
    }

    @Override
    public ImmutableList<CPlayer> getParticipants(@NonNull Channel channel) {
        List<CPlayer> players = new ArrayList<>();
        for (Map.Entry<CPlayer, Channel> cPlayerChannelEntry : this.activeChannels.entrySet()) {
            if (cPlayerChannelEntry.getValue().equals(channel)) players.add(cPlayerChannelEntry.getKey());
        }
        return ImmutableList.copyOf(players);
    }

    @Override
    public ImmutableList<CPlayer> getListeners(@NonNull Channel channel) {
        if (channel.equals(defaultChannel)) return ImmutableList.copyOf(Core.getOnlinePlayers());
        return ImmutableList.copyOf(this.listenerMap.get(channel));
    }

    @Override
    public void reload() throws ChannelException {
        this.listenerMap = new HashMap<>(); //Setup the listener map
        this.channels = new HashMap<>(); //Channel map
        for (Channel channel : this.configurationChannelSource.getNewChannels()) { //Load the channels from config
            registerChannel(channel); //by registering each one ^
            if (channel.isMarkedAsDefault()) this.defaultChannel = channel; //And setting the default up
        }
        if (defaultChannel == null) throw new IllegalStateException("There is no default channel!"); //And moaning if we can't get a default channel
        this.activeChannels = new HashMap<>();  //Create an activeChannels map
        this.messageArgumentDelegates = new ArrayList<>(); //and the delegates/
        this.messageProcessors = new ArrayList<>();//processors map
        this.chatterObservers = new ArrayList<>();
        for (ChannelManagerReloadObserver channelManagerReloadObserver : this.channelManagerReloadObservers) {
            channelManagerReloadObserver.onChannelManagerReload(this); //Let our observers know
        }
    }

    @Override
    public void save() {
        //TODO nothing
    }

    @Override
    public void registerChannel(@NonNull Channel channel) {
        if (this.channels.containsValue(channel)) throw new IllegalStateException("You cannot register the same channel twice!");
        this.channels.put(channel.getName(), channel);
        this.listenerMap.put(channel, new ArrayList<CPlayer>());
    }

    @Override
    public boolean isParticipating(@NonNull CPlayer player, @NonNull Channel channel) {
        /*
         * If we don't have them in the active channels map, they must be in the default channel, and as such they're only a participant if the channel being test is default
         * Otherwise, they're in a specific channel, which we need to do an equals comparison on.
         */
        return !this.activeChannels.containsKey(player) ? channel.equals(defaultChannel) : this.activeChannels.get(player).equals(channel);
    }

    @Override
    public boolean isListening(CPlayer player, Channel channel) {
        return channel.equals(defaultChannel) || this.listenerMap.get(channel).contains(player);
    }

    @Override
    public ImmutableList<MessageProcessor> getMessageProcessors() {
        return ImmutableList.copyOf(messageProcessors);
    }

    @Override
    public ImmutableList<MessageArgumentDelegate> getMessageArgumentDelegates() {
        return ImmutableList.copyOf(messageArgumentDelegates);
    }

    @Override
    public void registerMessageProcessor(MessageProcessor messageProcessor) {
        if (!this.messageProcessors.contains(messageProcessor)) this.messageProcessors.remove(messageProcessor);
    }

    @Override
    public void registerMessageArgumentDelegate(MessageArgumentDelegate messageArgumentDelegate) {
        if (!this.messageArgumentDelegates.contains(messageArgumentDelegate)) this.messageArgumentDelegates.add(messageArgumentDelegate);
    }

    @Override
    public void unregisterMessageProcessor(MessageProcessor messageProcessor) {
        if (this.messageProcessors.contains(messageProcessor)) this.messageProcessors.remove(messageProcessor);
    }

    @Override
    public void unregisterMessageArgumentDelegate(MessageArgumentDelegate messageArgumentDelegate) {
        if (this.messageArgumentDelegates.contains(messageArgumentDelegate)) this.messageArgumentDelegates.remove(messageArgumentDelegate);
    }

    @Override
    public void registerChannelManagerReloadObserver(ChannelManagerReloadObserver observer) {
        if (!this.channelManagerReloadObservers.contains(observer)) this.channelManagerReloadObservers.add(observer);
    }

    @Override
    public void unregisterChannelManagerReloadObserver(ChannelManagerReloadObserver observer) {
        if (this.channelManagerReloadObservers.contains(observer)) this.channelManagerReloadObservers.remove(observer);
    }

    @Override
    public void registerChatterObserver(ChatterObserver listener) {
        if (!this.chatterObservers.contains(listener)) this.chatterObservers.add(listener);
    }

    @Override
    public void unregisterChatterListener(ChatterObserver listener) {
        if (this.chatterObservers.contains(listener)) this.chatterObservers.remove(listener);
    }

    @Override
    public ImmutableList<ChatterObserver> getChatterObservers() {
        return ImmutableList.copyOf(chatterObservers);
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
        for (Channel channel : channels.values()) {
            /*
             * this is messy, have a close look
             * If the channel is auto participate, and the player can be a participant... *runs out of breath*
             * and the player is not currently participating in any other channel (except the default)
             * attempt to put them in the channel. Send them a message if they fail to join!
             */
            if (channel.isAutoParticipate() && channel.canBecomeParticipant(player) &&
                    getChannelPlayerParticipatingIn(player).equals(defaultChannel))  {
                try {
                    makePlayerParticipant(player, channel);
                } catch (ChannelException ignored) {
                }
            }
            /*
             * Repeat the same type of action for channels you can "listen" automatically.
             */
            if (channel.isAutoListen() && channel.canBecomeListener(player) && !channel.equals(defaultChannel) && !isListening(player, channel)) {
                try {
                    makePlayerListener(player, channel);
                } catch (ChannelException ignored) {
                }
            }
        }
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
    }
}
