package me.joeyandtom.communitycraft.core.player.mongo;

import lombok.*;
import me.joeyandtom.communitycraft.core.player.CPlayer;
import org.bukkit.Sound;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
public final class CMongoPlayer extends COfflineMongoPlayer implements CPlayer {

    @Getter private String lastSentChatMessage;

    public CMongoPlayer(List<String> knownUsernames, String lastKnownUsername, UUID uniqueIdentifier, List<String> knownIPAddresses, Date firstTimeOnline, Date lastTimeOnline, Long millisecondsOnline) {
        super(knownUsernames, lastKnownUsername, uniqueIdentifier, knownIPAddresses, firstTimeOnline, lastTimeOnline, millisecondsOnline);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public void sendMessage(String... messages) {

    }

    @Override
    public void sendFullChatMessage(String... messageLines) {

    }

    @Override
    public void clearChatAll() {

    }

    @Override
    public void clearChatVisible() {

    }

    @Override
    public void playSoundForPlayer(Sound s, Float volume, Float pitch) {

    }

    @Override
    public void playSoundForPlayer(Sound s, Float volume) {

    }

    @Override
    public void playSoundForPlayer(Sound s) {

    }
}
