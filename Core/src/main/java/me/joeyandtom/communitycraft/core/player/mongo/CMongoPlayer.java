package me.joeyandtom.communitycraft.core.player.mongo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.joeyandtom.communitycraft.core.player.CPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public final class CMongoPlayer extends COfflineMongoPlayer implements CPlayer {

    @Getter private String lastSentChatMessage;
    @Getter private Player bukkitPlayer;
    @Getter private boolean firstJoin = false;

    public CMongoPlayer(Player player, COfflineMongoPlayer offlinePlayer, CMongoPlayerManager manager) {
        super(offlinePlayer, manager);
        this.bukkitPlayer = player;
    }

    void onJoin() {
        this.setLastKnownUsername(bukkitPlayer.getName());
        this.setLastTimeOnline(new Date());
        addIfUnique(this.getKnownUsernames(), bukkitPlayer.getName());
        addIfUnique(this.getKnownIPAddresses(), bukkitPlayer.getAddress().getHostString());
        if (this.getFirstTimeOnline() == null) {
            this.setFirstTimeOnline(new Date());
            this.firstJoin = true;
        }
    }

    void onLeave() {
        Date leaveTime = new Date();
        this.setMillisecondsOnline(this.getMillisecondsOnline() + (leaveTime.getTime()-this.getLastTimeOnline().getTime()));
        this.setLastTimeOnline(leaveTime);
    }

    private static <T> void addIfUnique(List<T> list, T object) {
        for (T t : list) {
            if (t.equals(object)) return;
        }
        list.add(object);
    }

    @Override
    public String getName() {
        return bukkitPlayer.getName();
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
