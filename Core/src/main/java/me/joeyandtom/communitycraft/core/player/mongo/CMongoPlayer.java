package me.joeyandtom.communitycraft.core.player.mongo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.joeyandtom.communitycraft.core.player.CPlayer;
import me.joeyandtom.communitycraft.core.player.DatabaseConnectException;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(of = {"username"})
public final class CMongoPlayer extends COfflineMongoPlayer implements CPlayer {

    @Getter private String lastSentChatMessage;
    @Getter private final String username;
    @Getter private Player bukkitPlayer;
    @Getter private boolean firstJoin = false;

    public CMongoPlayer(Player player, COfflineMongoPlayer offlinePlayer, CMongoPlayerManager manager) {
        super(offlinePlayer, manager);
        this.username = player.getName();
        this.bukkitPlayer = player;
    }

    void onJoin() throws DatabaseConnectException {
        this.setLastKnownUsername(bukkitPlayer.getName());
        this.setLastTimeOnline(new Date());
        addIfUnique(this.getKnownUsernames(), bukkitPlayer.getName());
        String hostString = bukkitPlayer.getAddress().getHostString();
        addIfUnique(this.getKnownIPAddresses(), hostString);
        if (this.getFirstTimeOnline() == null) {
            this.setFirstTimeOnline(new Date());
            this.firstJoin = true;
        }
        saveIntoDatabase();
    }

    void updateForSaving() {
        Date leaveTime = new Date();
        //                                                         Current time (ex: 1000) minus the time you joined last (ex 50) (result 950 millis online)
        //                         current online time          +  The amount of time spent online this time -> milliseconds
        this.setMillisecondsOnline(this.getMillisecondsOnline() + (leaveTime.getTime()-this.getLastTimeOnline().getTime()));
        this.setLastTimeOnline(leaveTime);
    }

    private static <T> void addIfUnique(List<T> list, T object) {
        if (list == null) return;
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
        return bukkitPlayer.isOnline();
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) {
            bukkitPlayer.sendMessage(message);
        }
    }

    @Override
    public void sendFullChatMessage(String... messageLines) {
        int chatBufferSize = 10;
        String[] strings = new String[chatBufferSize];
        int deltaLength = strings.length - messageLines.length;
        double padding = Math.floor(deltaLength / 2);
        for (int x = (int)padding, y = 0; x < chatBufferSize-((deltaLength % 2 == 0) ? deltaLength : deltaLength-1); x++, y++) {
            strings[x] = messageLines[y];
        }
        for (String string : strings) {
            sendMessage(string);
        }
    }

    @Override
    public void clearChatAll() {
        for (int x = 0; x < 50; x++) {
            bukkitPlayer.sendMessage("");
        }
    }

    @Override
    public void clearChatVisible() {
        for (int x = 0; x < 20; x++) {
            bukkitPlayer.sendMessage("");
        }
    }

    @Override
    public void playSoundForPlayer(Sound s, Float volume, Float pitch) {
        bukkitPlayer.playSound(bukkitPlayer.getLocation(), s, volume, pitch);
    }

    @Override
    public void playSoundForPlayer(Sound s, Float volume) {
        playSoundForPlayer(s, volume, 0f);
    }

    @Override
    public void playSoundForPlayer(Sound s) {
        playSoundForPlayer(s, 10f);
    }
}
