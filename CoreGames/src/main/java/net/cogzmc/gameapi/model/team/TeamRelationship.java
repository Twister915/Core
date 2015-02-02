package net.cogzmc.gameapi.model.team;

@Value
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class TeamRelationship {
    private final Team team;
    private final Team target;
    private final TeamDisposition disposition;

    public static TeamRelationship makeRelationship(Team team, TeamDisposition disposition, Team target) {
        return new TeamRelationship(team, target, disposition);
    }
}
