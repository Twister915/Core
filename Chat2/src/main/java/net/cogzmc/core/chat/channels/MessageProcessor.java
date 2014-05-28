package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.CPlayer;

public interface MessageProcessor {
    String processChatMessage(CPlayer player, String message);
}
