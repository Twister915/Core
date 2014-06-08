package net.cogzmc.gameapi.model.team;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import net.cogzmc.core.player.CPlayer;

import java.util.HashSet;
import java.util.Set;

@Value
final class TeamMembership<TeamType extends Team> {
    private final CPlayer player;
    @Getter(AccessLevel.NONE) private final Set<TeamType> teams = new HashSet<>();

    public ImmutableSet<TeamType> getTeams() {
        return ImmutableSet.copyOf(teams);
    }

    public void addToTeam(TeamType team) {
        teams.add(team);
    }
}
