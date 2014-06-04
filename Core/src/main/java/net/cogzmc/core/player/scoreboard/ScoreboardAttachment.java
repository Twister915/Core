package net.cogzmc.core.player.scoreboard;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardObjective;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import lombok.*;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Data
public final class ScoreboardAttachment {
    private final static String TABLIST_POINTS_OBJECTIVE = "TABOBJPOINT";
    private final static String SIDEBAR_OBJECTIVE = "SIDEBAROBJ";

    private final CPlayer player;
    @Getter(AccessLevel.PACKAGE) private final Map<String, Integer> text = new HashMap<>();
    private String prefix = "";
    private String suffix = "";
    private String sideTitle = "Scoreboard";

    public ScoreboardAttachment(CPlayer player) {
        this.player = player;
        Bukkit.getScheduler().runTask(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                createSidebarObjective();
            }
        });
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        updateTeam();
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
        updateTeam();
    }

    public void setSideText(@NonNull String string, @NonNull Integer integer) {
        this.text.put(string, integer);
        createOrUpdateSidebarText(string, integer);
    }

    public void setSideTitle(String title) {
        this.sideTitle = title;
        updateTitle();
    }

    public void setListPoints(Integer points) {
        throw new NotImplementedException("Ain't done yet");
    }

    public void removeSideItem(String item) {
        if (!this.text.containsKey(item)) throw new IllegalArgumentException("This is not in the scoreboard!");
        this.text.remove(item);

    }

    private void updateTeam() {
        ScoreboardTeam team = ScoreboardManager.getInstance().getTeamForPrefixAndSuffix(prefix, suffix);
        ScoreboardManager.getInstance().setPlayerTeam(player, team);
    }

    private void createSidebarObjective() {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setPacketMode((byte) WrapperPlayServerScoreboardObjective.Modes.ADD_OBJECTIVE);
        packet.setObjectiveName(SIDEBAR_OBJECTIVE);
        packet.setObjectiveValue(sideTitle);

        WrapperPlayServerScoreboardDisplayObjective displayPacket = new WrapperPlayServerScoreboardDisplayObjective();
        displayPacket.setPosition((byte) WrapperPlayServerScoreboardDisplayObjective.Positions.SIDEBAR);
        displayPacket.setScoreName(SIDEBAR_OBJECTIVE);

        packet.sendPacket(player.getBukkitPlayer());
        displayPacket.sendPacket(player.getBukkitPlayer());
    }

    private void createOrUpdateSidebarText(String string, Integer integer) {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setScoreName(SIDEBAR_OBJECTIVE);
        packet.setPacketMode((byte) WrapperPlayServerScoreboardScore.Modes.SET_SCORE);
        packet.setItemName(string);
        packet.setValue(integer);
        packet.sendPacket(player.getBukkitPlayer());
    }

    private void updateTitle() {
        WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective();
        packet.setPacketMode((byte) WrapperPlayServerScoreboardObjective.Modes.UPDATE_VALUE);
        packet.setObjectiveName(SIDEBAR_OBJECTIVE);
        packet.setObjectiveValue(sideTitle);
        packet.sendPacket(player.getBukkitPlayer());
    }

    private void removeSidebarText(String key) {
        WrapperPlayServerScoreboardScore scorePacket = new WrapperPlayServerScoreboardScore();
        scorePacket.setPacketMode((byte) WrapperPlayServerScoreboardScore.Modes.REMOVE_SCORE);
        scorePacket.setItemName(key);
        scorePacket.setScoreName(SIDEBAR_OBJECTIVE);
        scorePacket.sendPacket(player.getBukkitPlayer());
    }
}
