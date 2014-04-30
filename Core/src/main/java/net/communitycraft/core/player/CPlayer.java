package net.communitycraft.core.player;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public interface CPlayer extends COfflinePlayer {
    String getName();
    boolean isOnline();
    boolean isFirstJoin();

    void sendMessage(String... messages);
    void sendFullChatMessage(String... messageLines);
    void clearChatAll();
    void clearChatVisible();
    String getLastSentChatMessage();

    void playSoundForPlayer(Sound s, Float volume, Float pitch);
    void playSoundForPlayer(Sound s, Float volume);
    void playSoundForPlayer(Sound s);

    Player getBukkitPlayer();
}
