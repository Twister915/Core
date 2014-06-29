package net.cogzmc;

import lombok.NonNull;
import lombok.ToString;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@ToString
public class PlayerBean {
    public PlayerBean() {}

    @XmlElement(name = "username") public String currentUsername;
    public String[] usernames;
    public String[] ips;
    public String uuid;
    public Date firstTimeOnline;
    public Date lastTimeSeen;
    public Long millisecondsOnline;
    public String displayName;

    public PlayerBean(@NonNull COfflinePlayer player) {
        this.currentUsername = player.getLastKnownUsername();
        this.usernames = toArray(player.getKnownUsernames(), String.class);
        this.ips = toArray(player.getKnownIPAddresses(), String.class);
        this.uuid = player.getUniqueIdentifier().toString();
        this.firstTimeOnline = player.getFirstTimeOnline();
        this.lastTimeSeen = player.getLastTimeOnline();
        this.millisecondsOnline = player.getMillisecondsOnline();
        this.displayName = player.getDisplayName();
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