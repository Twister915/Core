package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.CPlayer;

public interface ChatterObserver {
    void onMessageSent(CPlayer sender, Channel channel, String message);
}
