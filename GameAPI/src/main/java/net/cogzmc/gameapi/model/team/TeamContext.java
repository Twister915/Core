package net.cogzmc.gameapi.model.team;

import com.google.common.collect.ImmutableSet;
import net.cogzmc.core.player.CPlayer;

import java.util.*;

public final class TeamContext<TeamType extends Team> {
    private final Set<TeamType> teams = new HashSet<>();
    private final Set<TeamRelationship> teamRelationships = new HashSet<>();
    private final Map<CPlayer, TeamMembership<TeamType>> teamMemberships = new HashMap<>();
    private final TeamType defaultTeam;

    @SafeVarargs
    public TeamContext(TeamType defaultTeam, TeamType... teams) {
        this.defaultTeam = defaultTeam;
        this.teams.add(defaultTeam);
        if (teams.length > 0) Collections.addAll(this.teams, teams);
    }

    public void makePlayerTeamMember(TeamType team, CPlayer player) {
        if (!teams.contains(team)) throw new IllegalArgumentException("The team passed must be a member team.");
        if (!team.canJoin(player)) throw new IllegalArgumentException("Player cannot join this team!");
        teamMemberships.get(player).addToTeam(team);
    }

    public TeamDisposition getRelationship(TeamType team, TeamType target) {
        if (!(teams.contains(team) && teams.contains(target))) throw new IllegalArgumentException("The teams passed must be members of the teams Set");
        for (TeamRelationship teamRelationship : teamRelationships) {
            if (teamRelationship.getTeam().equals(team) && teamRelationship.getTarget().equals(target)) return teamRelationship.getDisposition();
        }
        return TeamDisposition.NETURAL;
    }

    public void setTeamRelationship(TeamType team, TeamDisposition disposition, TeamType target) {
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

    public ImmutableSet<TeamType> getTeamsFor(CPlayer player) {
        TeamMembership<TeamType> teamMembership = teamMemberships.get(player);
        if (teamMembership.getTeams().size() == 0) return ImmutableSet.copyOf(Arrays.asList(defaultTeam));
        return teamMembership.getTeams();
    }
}
