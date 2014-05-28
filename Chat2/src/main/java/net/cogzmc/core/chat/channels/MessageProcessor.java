package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.COfflinePlayer;

public interface MessageProcessor {
    String processChatMessage(COfflinePlayer player, String message);
}
