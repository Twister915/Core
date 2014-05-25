package net.cogzmc.core.player.mongo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CooldownManager;
import net.cogzmc.core.player.DatabaseConnectException;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true, of = {"username"})
@ToString(of = {"username"})
final class CMongoPlayer extends COfflineMongoPlayer implements CPlayer {

    @Getter private final String username;
    @Getter private Player bukkitPlayer;
    private PermissionAttachment permissionAttachment;
    @Getter private boolean firstJoin = false;
    @Getter private InetAddress address = null;
    @Getter private final CooldownManager cooldownManager = new CooldownManager();

    public CMongoPlayer(Player player, COfflineMongoPlayer offlinePlayer, CMongoPlayerManager manager) {
        super(offlinePlayer, manager);
        this.username = player.getName();
        this.bukkitPlayer = player;
    }

    void onJoin(InetAddress address) throws DatabaseConnectException {
        this.setLastKnownUsername(bukkitPlayer.getName());
        this.setLastTimeOnline(new Date());
        addIfUnique(this.getKnownUsernames(), bukkitPlayer.getName());
        this.address = address;
        String hostString = address.getHostAddress();
        addIfUnique(this.getKnownIPAddresses(), hostString);
        if (this.getFirstTimeOnline() == null) {
            this.setFirstTimeOnline(new Date());
            this.firstJoin = true;
        }
        saveIntoDatabase();
        reloadPermissions();
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
        if (deltaLength < 0) throw new IllegalArgumentException("There are too many messages for this chat buffer");
        //Padding is the difference in the size of the chat buffer and the size of the message divided by two
        //Example, 8 lines should have a buffer of one on each side (top/bottom). 10-8 = 2, 2/2 = 1. The buffer should be one, and padding = 1!
        //Example 2, 9 lines should have a buffer of 0 on the top and 1 on the bottom. 10-9 = 1, 1/2 = 0.5, buffer = 0, padding = 0
        //Example 3, 5 lines should have a buffer of 2 one one side, 3 on another. 10-5 = 5, 5/2 = 2.5, 2.5 -> 2, buffer = 2, padding = 2
        double padding = Math.floor(deltaLength / 2);
        //x will start at the padding, the lower end of the buffer.
        //x will go until we reach the end of the strings by testing if it is at padding+messageLines.length
        //y tracks the index int the messageLines array.
        for (int x = (int)padding, y = 0; x < padding+messageLines.length; x++, y++) {
            strings[x] = messageLines[y];
        }
        for (String string : strings) {
            if (string == null) sendMessage("");
            else sendMessage(string);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return bukkitPlayer.hasPermission(permission);
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

    @Override
    public void reloadPermissions() {
        super.reloadPermissions();
        if (permissionAttachment != null) permissionAttachment.remove();
        permissionAttachment = bukkitPlayer.addAttachment(Core.getInstance());
        for (Map.Entry<String, Boolean> stringBooleanEntry : getAllPermissions().entrySet()) {
            permissionAttachment.setPermission(stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
    }
}
