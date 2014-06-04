package net.cogzmc.core.player.scoreboard;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import lombok.*;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public final class ScoreboardTeam {
    @NonNull private final String name;
    @NonNull private String displayName;
    private String prefix;
    private String suffix;
    private final List<CPlayer> players = new ArrayList<>();

    void playerDisconnected(CPlayer player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            ScoreboardManager.getInstance().broadcastPacket(getTeamPacket((byte) WrapperPlayServerScoreboardTeam.Modes.PLAYERS_REMOVED, Arrays.asList(player.getName())));
        }
    }

    void addPlayer(CPlayer player) {
        if (this.players.contains(player)) throw new IllegalArgumentException("You cannot add a player to this team because they are already a member of that team.");
        this.players.add(player);
        Core.logInfo("Broadcasting an add player packet.");
        ScoreboardManager.getInstance().broadcastPacket(getTeamPacket((byte) WrapperPlayServerScoreboardTeam.Modes.PLAYERS_ADDED, Arrays.asList(player.getName())));
    }

    void removePlayer(CPlayer player) {
        if (!this.players.contains(player)) throw new IllegalArgumentException("You cannot remove a player from this team if they're not already a member of this team!");
        this.players.remove(player);
        ScoreboardManager.getInstance().broadcastPacket(getTeamPacket((byte) WrapperPlayServerScoreboardTeam.Modes.PLAYERS_REMOVED, Arrays.asList(player.getName())));
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        ScoreboardManager.getInstance().broadcastPacket(getTeamPacket((byte) WrapperPlayServerScoreboardTeam.Modes.TEAM_UPDATED));
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
        ScoreboardManager.getInstance().broadcastPacket(getTeamPacket((byte) WrapperPlayServerScoreboardTeam.Modes.TEAM_UPDATED));
    }

    WrapperPlayServerScoreboardTeam getTeamPacket(Byte mode, List<String> playerDelta) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setPacketMode(mode);
        packet.setTeamName(name);
        switch (mode) {
            case 0:
            case 2:
                packet.setTeamSuffix(suffix);
                packet.setTeamPrefix(prefix);
                packet.setTeamDisplayName(displayName);
                packet.setFriendlyFire((byte) 0);
            case 3:
            case 4:
                if (mode != 2) {
                    Core.logInfo("Placing players for mode " + mode);
                    packet.setPlayers(playerDelta);
                }
        }
        Core.logInfo("Sending team packet for mode " + mode);
        return packet;
    }

    WrapperPlayServerScoreboardTeam getTeamPacket(Byte mode) {
        if (mode == 3 || mode == 4) throw new IllegalArgumentException("You must specify a player delta for this to work!");
        return getTeamPacket(mode, null);
    }

}
