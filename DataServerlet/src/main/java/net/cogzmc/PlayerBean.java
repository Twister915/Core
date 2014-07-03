package net.cogzmc;

import lombok.NonNull;
import lombok.ToString;
import net.cogzmc.core.player.CGroup;
import net.cogzmc.core.player.COfflinePlayer;
import org.ocpsoft.prettytime.PrettyTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

@XmlRootElement
@ToString
public final class PlayerBean {
    public PlayerBean() {}

    @XmlElement(name = "username") public String current_username;
    public String[] usernames;
    public String[] ips;
    public String uuid;
    public Date first_time_online;
    public Date last_time_seen;
    public Long milliseconds_online;
    public String display_name;
    public String[] groups;
    public String prefix;
    public String suffix;
    public String tab_color;
    public String chat_color;
    public String primary_group;

    public String pretty_last_seen;
    public String pretty_first_join;

    public PlayerBean(@NonNull COfflinePlayer player) {
        this.current_username = player.getLastKnownUsername();
        this.usernames = toArray(player.getKnownUsernames(), String.class);
        this.ips = toArray(player.getKnownIPAddresses(), String.class);
        this.uuid = player.getUniqueIdentifier().toString();
        this.first_time_online = player.getFirstTimeOnline();
        this.last_time_seen = player.getLastTimeOnline();
        this.milliseconds_online = player.getMillisecondsOnline();
        this.display_name = player.getDisplayName();
        List<CGroup> groups1 = player.getGroups();
        String[] groupNames = new String[groups1.size()];
        for (int x = 0; x < groups1.size(); x++) {
            groupNames[x] = groups1.get(x).getName();
        }
        this.groups = groupNames;
        this.prefix = player.getChatPrefix();
        this.suffix = player.getChatSuffix();
        this.chat_color = player.getChatColor();
        this.tab_color = player.getTablistColor();
        this.primary_group = player.getPrimaryGroup().getName();
        PrettyTime prettyTime = new PrettyTime();
        this.pretty_last_seen = prettyTime.format(this.last_time_seen);
        this.pretty_first_join = prettyTime.format(this.first_time_online);
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