package net.cogzmc.core.chat.channels;

public interface MessageProcessor {
    String processChatMessage(COfflinePlayer player, String message);
}
