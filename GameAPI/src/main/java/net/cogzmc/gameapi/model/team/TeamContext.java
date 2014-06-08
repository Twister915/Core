package net.cogzmc.gameapi.model.team;

import com.google.common.collect.ImmutableSet;
import net.cogzmc.core.player.CPlayer;

import java.util.*;

public final class TeamContext {
    private final Set<Team> teams = new HashSet<>();
    private final Set<TeamRelationship> teamRelationships = new HashSet<>();
    private final Map<CPlayer, TeamMembership> teamMemberships = new HashMap<>();
    private final Team defaultTeam;

    public TeamContext(Team defaultTeam, Team... teams) {
        this.defaultTeam = defaultTeam;
        this.teams.add(defaultTeam);
        if (teams.length > 0) Collections.addAll(this.teams, teams);
    }

    public void makePlayerTeamMember(Team team, CPlayer player) {
        if (!teams.contains(team)) throw new IllegalArgumentException("The team passed must be a member team.");
        teamMemberships.get(player).addToTeam(team);
    }

    public TeamDisposition getRelationship(Team team, Team target) {
        if (!(teams.contains(team) && teams.contains(target))) throw new IllegalArgumentException("The teams passed must be members of the teams Set");
        for (TeamRelationship teamRelationship : teamRelationships) {
            if (teamRelationship.getTeam().equals(team) && teamRelationship.getTarget().equals(target)) return teamRelationship.getDisposition();
        }
        return TeamDisposition.NETURAL;
    }

    public void setTeamRelationship(Team team, TeamDisposition disposition, Team target) {
        if (!(teams.contains(team) && teams.contains(target))) throw new IllegalArgumentException("The teams passed must be members of the teams Set");
        if (disposition == TeamDisposition.NETURAL) {
            Iterator<TeamRelationship> iterator = teamRelationships.iterator();
            while (iterator.hasNext()) {
                TeamRelationship relationship = iterator.next();
                if (relationship.getTarget().equals(team) && relationship.getTarget().equals(target)) iterator.remove();
            }
            return;
        }
        teamRelationships.add(TeamRelationship.makeRelationship(team, disposition, target));
    }

    public ImmutableSet<Team> getTeamsFor(CPlayer player) {
        TeamMembership teamMembership = teamMemberships.get(player);
        if (teamMembership.getTeams().size() == 0) return ImmutableSet.copyOf(Arrays.asList(defaultTeam));
        return ImmutableSet.copyOf(teamMembership.getTeams());
    }
}
