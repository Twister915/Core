package net.cogzmc.gameapi.model.team;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(of = {"team", "target"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeamRelationship {
    private final Team team;
    private final Team target;
    private final TeamDisposition disposition;

    public static TeamRelationship makeRelationship(Team team, TeamDisposition disposition, Team target) {
        return new TeamRelationship(team, target, disposition);
    }
}
