package net.cogzmc.core.chat.channels;

public interface Channel {
    String formatMessage(COfflinePlayer sender, String chatMessage);
    String getName();
    boolean canBecomeListener(CPlayer player);
    boolean canBecomeParticipant(CPlayer player);
    boolean isAutoParticipate();
    boolean isAutoListen();
    boolean isMarkedAsDefault();
    boolean isCrossServer();
    boolean canRemoveListener(CPlayer player);
    boolean canRemoveParticipant(CPlayer player);
}
