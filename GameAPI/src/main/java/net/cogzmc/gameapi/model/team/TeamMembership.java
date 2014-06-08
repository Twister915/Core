package net.cogzmc.gameapi.model.team;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import net.cogzmc.core.player.CPlayer;

import java.util.HashSet;
import java.util.Set;

@Value
public final class TeamMembership {
    private final CPlayer player;
    @Getter(AccessLevel.NONE) private final Set<Team> teams = new HashSet<>();

    public ImmutableSet<Team> getTeams() {
        return ImmutableSet.copyOf(teams);
    }

    public void addToTeam(Team team) {
        teams.add(team);
    }
}
