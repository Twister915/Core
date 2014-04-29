package me.joeyandtom.communitycraft.core.player;

import org.bukkit.Sound;

public interface CPlayer extends COfflinePlayer {
    boolean isOnline();

    void sendMessage(String... messages);
    void sendFullChatMessage(String... messageLines);
    void clearChatAll();
    void clearChatVisible();
    String getLastSentChatMessage();

    void playSoundForPlayer(Sound s, Float volume, Float pitch);
    void playSoundForPlayer(Sound s, Float volume);
    void playSoundForPlayer(Sound s);
}
