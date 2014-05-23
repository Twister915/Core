package net.cogzmc.chat;

import lombok.Getter;
import net.cogzmc.chat.channels.ChannelManager;
import net.cogzmc.chat.channels.ChannelsListener;
import net.cogzmc.chat.channels.commands.ChannelCommand;
import net.cogzmc.chat.channels.commands.ChannelsCommand;
import net.cogzmc.chat.data.Chat;
import net.cogzmc.chat.filter.CensorsCommand;
import net.cogzmc.chat.management.ChatCommand;
import net.cogzmc.core.config.YAMLConfigurationFile;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;

import java.util.List;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/21/2014
 */
@ModuleMeta(
        name = "Chat Manager",
        description = "Manages player chat, including formatting, cross server chat, and filtering of it."
)
public class ChatManager extends ModularPlugin {
    @Getter private static ChatManager instance;
    /**
     * Config were channel data can be stored. (Ex. formats, settings, etc)
     */
    @Getter private YAMLConfigurationFile channelsConfig;
    /**
     * Chat data class for storing information about chat such as global mute status,
     * last sent messages, and other information.
     */
    @Getter private Chat chat;
    /**
     * The ChannelManager manages players and their channels.
     */
    @Getter private ChannelManager channelManager;

    @Override
    public void onModuleEnable() {
        ChatManager.instance = this;
        //Create config
        this.channelsConfig = new YAMLConfigurationFile(this, "channels.yml");
        this.channelsConfig.reloadConfig();
        this.channelsConfig.saveDefaultConfig();
        //Create instances of managers
        this.chat = new Chat();
        this.channelManager = new ChannelManager();
        //Register commands and listeners
        registerCommand(new ChannelCommand());
        registerCommand(new ChannelsCommand());
        registerListener(new ChannelsListener(channelManager));
        registerCommand(new ChatCommand());
        registerCommand(new CensorsCommand());
        //Register channels
        this.channelManager.registerChannels();
    }

    /**
     * Return a list of {@link net.cogzmc.chat.filter.CensoredWord}s
     *
     * @return {@link java.util.List} of {@link net.cogzmc.chat.filter.CensoredWord}s
     */
    public String[] getCensoredWords() {
        List<String> censored = getConfig().getStringList("censored");
        return censored.toArray(new String[censored.size()]);
    }

    /**
     * Sets the censored words to the specified parameter
     *
     * @param censored list of {@link net.cogzmc.chat.filter.CensoredWord} to set the list to.
     */
    private void setCensoredWords(String[] censored) {
        getConfig().set("censored", censored);
    }
}
