package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.COfflinePlayer;

import java.util.Map;

public interface MessageArgumentDelegate {
    Map<String, String> getArgumentsFor(COfflinePlayer player, String message);
}
