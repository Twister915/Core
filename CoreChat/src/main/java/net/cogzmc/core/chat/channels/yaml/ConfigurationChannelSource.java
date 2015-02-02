package net.cogzmc.core.chat.channels.yaml;

import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ChannelException;
import net.cogzmc.core.chat.channels.ChannelSource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

@Data
public final class ConfigurationChannelSource implements ChannelSource {
    private final ConfigurationChannelManager manager;
    @Override
    public List<Channel> getNewChannels() throws ChannelException {
        CoreChat coreChat = CoreChat.getInstance();
        YAMLConfigurationFile defaultChannel = new YAMLConfigurationFile(coreChat, "default.yml");
        defaultChannel.saveDefaultConfig();
        defaultChannel.saveConfig();
        String[] list = coreChat.getDataFolder().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.startsWith("formats") && !name.startsWith("config") && name.endsWith(".yml");
            }
        });
        List<Channel> configChannels = new ArrayList<>();
        for (String s : list) {
            YAMLConfigurationFile yamlConfigurationFile = new YAMLConfigurationFile(coreChat, s);
            configChannels.add(new ConfigurationChannel(yamlConfigurationFile.getConfig(), manager));
        }
        return configChannels;
    }
}
