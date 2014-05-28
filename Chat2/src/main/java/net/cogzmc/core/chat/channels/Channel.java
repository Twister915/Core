package net.cogzmc.core.chat.channels;

import net.cogzmc.core.player.CPlayer;

public interface Channel {
    String formatMessage(CPlayer sender, String chatMessage);
    String getName();
    boolean canBecomeListener(CPlayer player);
    boolean canBecomeParticipant(CPlayer player);
    boolean isAutoParticipate();
    boolean isAutoListen();
    boolean canRemoveListener(CPlayer player);
    boolean canRemoveParticipant(CPlayer player);
}
