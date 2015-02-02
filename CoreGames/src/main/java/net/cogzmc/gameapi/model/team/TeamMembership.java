package net.cogzmc.gameapi.model.team;

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

    public boolean isMemberOfTeam(TeamType team) {
        return teams.contains(team);
    }

    public void removeFromTeam(TeamType team) {
        teams.remove(team);
    }
}
