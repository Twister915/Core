package net.cogzmc;

import lombok.NonNull;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
public final class PlayerBean {
    public final String currentUsername;
    public final String[] usernames;
    public final String[] ips;
    public final String uuid;
    public final Date firstTimeOnline;
    public final Date lastTimeSeen;
    public final Long millisecondsOnline;
    public final Map<String, Object> settings;
    public final String[] groups;
    public final String primaryGroup;
    public final String displayName;
    public final String prefix;
    public final String suffix;
    public final Map<String, Boolean> declaredPermissions;

    public PlayerBean(@NonNull COfflinePlayer player) {
        this.currentUsername = player.getLastKnownUsername();
        this.usernames = toArray(player.getKnownUsernames(), String.class);
        this.ips = toArray(player.getKnownIPAddresses(), String.class);
        this.uuid = player.getUniqueIdentifier().toString();
        this.firstTimeOnline = player.getFirstTimeOnline();
        this.lastTimeSeen = player.getLastTimeOnline();
        this.millisecondsOnline = player.getMillisecondsOnline();
        this.settings = new HashMap<>();
        for (String s : player.getSettingKeys()) {
            settings.put(s, player.getSettingValue(s, Object.class));
        }
        List<CGroup> groups1 = player.getGroups();
        this.groups = new String[groups1.size()];
        for (int x = 0; x < groups1.size(); x++) {
            groups[x] = groups1.get(x).getName();
        }
        this.primaryGroup = player.getPrimaryGroup().getName();
        this.prefix = player.getChatPrefix();
        this.suffix = player.getChatSuffix();
        this.displayName = player.getDisplayName();
        this.declaredPermissions = player.getDeclaredPermissions();
    }

    public static <T> T[] toArray(@NonNull List<T> ts, @NonNull Class<T> elementType) {
        @SuppressWarnings("unchecked")
        T[] ts1 = (T[]) Array.newInstance(elementType, ts.size());
        for (int x = 0; x < ts.size(); x++) {
            ts1[x] = ts.get(x);
        }
        return ts1;
    }
}