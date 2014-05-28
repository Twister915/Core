package net.cogzmc.core.chat.channels.yaml;

import lombok.Data;
import net.cogzmc.core.chat.CoreChat;
import net.cogzmc.core.chat.channels.Channel;
import net.cogzmc.core.chat.channels.ChannelException;
import net.cogzmc.core.chat.channels.ChannelSource;
import net.cogzmc.core.config.YAMLConfigurationFile;

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
        File dataFolder = coreChat.getDataFolder();
        File channels = new File(dataFolder, "channels");
        if (!channels.isDirectory() && channels.mkdir()) {
            YAMLConfigurationFile yamlConfigurationFile = new YAMLConfigurationFile(coreChat, new File(channels, "default.yml"));
            yamlConfigurationFile.saveDefaultConfig();
            ConfigurationChannel defaultChannel = new ConfigurationChannel(yamlConfigurationFile.getConfig(), manager);
            ArrayList<Channel> channelsReturnVal = new ArrayList<>();
            channelsReturnVal.add(defaultChannel);
            return channelsReturnVal;
        }
        String[] list = channels.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });
        List<Channel> configChannels = new ArrayList<>();
        for (String s : list) {
            File channelFile = new File(channels, s);
            YAMLConfigurationFile yamlConfigurationFile = new YAMLConfigurationFile(coreChat, channelFile);
            configChannels.add(new ConfigurationChannel(yamlConfigurationFile.getConfig(), manager));
        }
        return configChannels;
    }
}
