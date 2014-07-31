package net.cogzmc.core.player.scoreboard;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public final class ScoreboardManager implements Listener, CPlayerConnectionListener {
    private List<ScoreboardTeam> scoreboardTeams = new ArrayList<>();

    @Getter private static ScoreboardManager instance;

    public ScoreboardManager() {
        instance = this;
    }

    private ScoreboardTeam createTeam(String name) {
        ScoreboardTeam scoreboardTeam = new ScoreboardTeam(name, name);
        this.scoreboardTeams.add(scoreboardTeam);
        broadcastCreatePacket(scoreboardTeam);
        return scoreboardTeam;
    }

    public void setPlayerTeam(CPlayer player, ScoreboardTeam team) {
        ScoreboardTeam playersTeam = getPlayersTeam(player);
        if (playersTeam != null) playersTeam.removePlayer(player);
        Core.logInfo("Setting a player team!");
        team.addPlayer(player);
    }

    public ScoreboardTeam getTeamForPrefixAndSuffix(String prefix, String suffix) {
        for (ScoreboardTeam scoreboardTeam : scoreboardTeams) {
            if (scoreboardTeam.getPrefix().equals(prefix) && scoreboardTeam.getSuffix().equals(suffix)) return scoreboardTeam;
        }
        ScoreboardTeam team = null;
        do { //Ensure unique and generate a random name
            float id = Math.round(Core.getRandom().nextFloat() * 1000);
            String identifierName = String.format("%.0f", id);
            boolean fail = false;
            for (ScoreboardTeam scoreboardTeam : scoreboardTeams) {
                if (scoreboardTeam.getName().equals(identifierName)) {
                    fail = true;
                    break;
                }
            }
            if (fail) continue;
            Core.logDebug("Creating a new team for prefix " + prefix + " and suffix " + suffix);
            team = createTeam(identifierName);
            Core.logDebug("Setting prefix of new team " + team.getName() + " to " + prefix);
            team.setPrefix(prefix);
            Core.logDebug("Setting suffix of new team " + team.getName() + " to " + suffix);
            team.setSuffix(suffix);
        } while (team == null);
        return team;
    }

    public ScoreboardTeam getPlayersTeam(CPlayer player) {
        for (ScoreboardTeam scoreboardTeam : scoreboardTeams) {
            if (scoreboardTeam.getPlayers().contains(player)) return scoreboardTeam;
        }
        return null;
    }

    void broadcastPacket(AbstractPacket packet) {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet.getHandle());
    }

    //Event handlers
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (ScoreboardTeam scoreboardTeam : scoreboardTeams) {
            broadcastCreatePacket(scoreboardTeam);
        }
    }

    private void broadcastCreatePacket(ScoreboardTeam team) {
        Core.logDebug("Creating team " + team.getName());
        team.getTeamPacket((byte) WrapperPlayServerScoreboardTeam.Modes.TEAM_CREATED);
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
        //IGNORE
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        for (ScoreboardTeam scoreboardTeam : scoreboardTeams) {
            if (scoreboardTeam.getPlayers().contains(player)) scoreboardTeam.playerDisconnected(player);
        }
    }
}
