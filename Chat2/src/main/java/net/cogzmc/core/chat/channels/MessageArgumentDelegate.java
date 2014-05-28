package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.CPlayer;

import java.util.Map;

public interface MessageArgumentDelegate {
    Map<String, String> getArgumentsFor(CPlayer player, String message);
}
