package net.cogzmc.core.chat.channels.yaml;

import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ChannelException;
import net.cogzmc.core.chat.channels.MessageArgumentDelegate;
import net.cogzmc.core.chat.channels.MessageProcessor;

import java.util.HashMap;
import java.util.Map;

public final class ConfigurationChannel implements Channel {
    private static enum ConfigKeys {
        FORMAT("format"),
        NAME("name"),
        DEFAULT("default"),
        CROSS_SERVER("cross-server"),
        AUTO_PARTICIPATE("auto-join"),
        AUTO_LISTEN("auto-listen");
        private final String value;
        ConfigKeys(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return value;
        }
    }

    private final String formatString;
    @Getter private final String name;
    private final boolean defaultChannel;
    @Getter private final boolean autoParticipate;
    @Getter private final boolean autoListen;
    @Getter private final boolean crossServer;
    private final ConfigurationChannelManager channelManager;

    public ConfigurationChannel(FileConfiguration yamlFile, ConfigurationChannelManager channelManager) throws ChannelException {
        this.name = yamlFile.getString(ConfigKeys.NAME.toString());
        this.defaultChannel = yamlFile.getBoolean(ConfigKeys.DEFAULT.toString(), false);
        this.formatString = ChatColor.translateAlternateColorCodes('&', yamlFile.getString(ConfigKeys.FORMAT.toString()));
        this.autoParticipate = yamlFile.getBoolean(ConfigKeys.AUTO_PARTICIPATE.toString(), false);
        this.autoListen = yamlFile.getBoolean(ConfigKeys.AUTO_LISTEN.toString(), false);
        this.crossServer = yamlFile.getBoolean(ConfigKeys.CROSS_SERVER.toString(), false);
        this.channelManager = channelManager;
    }
    @Override
    public String formatMessage(COfflinePlayer sender, String chatMessage) {
        return StrSubstitutor.replace(formatString, getSirSubstituteMap(sender, chatMessage), "{$", "}");
    }

    @Override
    public boolean canBecomeListener(CPlayer player) {
        return player.hasPermission(CoreChat.getChannelListenPermission(this));
    }

    @Override
    public boolean canBecomeParticipant(CPlayer player) {
        return player.hasPermission(CoreChat.getChannelJoinPermission(this));
    }

    @Override
    public boolean canRemoveListener(CPlayer player) {
        return player.hasPermission(CoreChat.getChannelUnlistenPermission(this));
    }

    @Override
    public boolean canRemoveParticipant(CPlayer player) {
        return player.hasPermission(CoreChat.getChannelLeavePermission(this));
    }

    private Map<String, String> getSirSubstituteMap(COfflinePlayer player, String message) {
        HashMap<String, String> values = new HashMap<>();
        for (MessageProcessor messageProcessor : this.channelManager.getMessageProcessors()) {
            message = messageProcessor.processChatMessage(player, message);
        }
        values.put("sender-actual", player.getName());
        values.put("sender-display", player.getDisplayName());
        String prefix = player.getChatPrefix() == null ? player.getPrimaryGroup() == null ? "" : player.getPrimaryGroup().getChatPrefix() : player.getChatPrefix();
        String suffix = player.getChatSuffix() == null ? player.getPrimaryGroup() == null ? "" : player.getPrimaryGroup().getChatSuffix() : player.getChatSuffix();
        String nameColor = player.getChatColor() == null ? player.getPrimaryGroup() == null ? "" : player.getPrimaryGroup().getChatColor() : player.getChatColor();
        values.put("prefix", ChatColor.translateAlternateColorCodes('&',prefix));
        values.put("suffix", ChatColor.translateAlternateColorCodes('&',suffix));
        values.put("name-color", ChatColor.translateAlternateColorCodes('&',nameColor));
        values.put("message", player.hasPermission(CoreChat.COLOR_CHAT_PERMISSION) ? ChatColor.translateAlternateColorCodes('&', message) : message);
        values.put("channel-name", name);
        for (MessageArgumentDelegate messageArgumentDelegate : this.channelManager.getMessageArgumentDelegates()) {
            for (Map.Entry<String, String> stringStringEntry : messageArgumentDelegate.getArgumentsFor(player, message).entrySet()) {
                values.put(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }
        return values;
    }

    @Override
    public boolean isMarkedAsDefault() {
        return defaultChannel;
    }
}
