package net.cogzmc.core.effect.npc.pathfinding;

import net.cogzmc.core.util.Point;

@Data
@EqualsAndHashCode(of = {"point", "parent"})
@Setter(AccessLevel.PRIVATE)
public final class PathTile {
    private final static Integer STRAIGHT_MOVEMENT_SCORE = 10; //1*10
    private final static Integer DIAGONAL_MOVEMENT_SCORE = 14; //root2*10

    @NonNull private final Point point;
    private final PathTile parent;

    private Integer fScore;
    private Integer gScore;
    private Integer hScore;
    private boolean ordinalMovement;

    void updateScores(PathTile start, PathTile end) {
        PathTile current = getParent();
        int gScoreCombined = 0;
        //IntelliJ thinks that it's impossible for current to != null, as it's always set, however the value of the method could be null!
        //noinspection ConstantConditions
        while (!current.equals(start) && current != null) {
            gScoreCombined += current.getGScore();
            current = current.getParent();
        }
        ordinalMovement = isOrdinalMovement(parent, this);
        if (ordinalMovement) gScore = gScoreCombined + STRAIGHT_MOVEMENT_SCORE;
        else gScore = gScoreCombined + DIAGONAL_MOVEMENT_SCORE;

        //hScore time!
        hScore = ((int) Math.ceil(end.getPoint().distanceSquared(point)));
        fScore = hScore + gScore;
    }

    private static boolean isOrdinalMovement(PathTile start, PathTile destination) {
        Point startPoint = start.getPoint(), endPoint = destination.getPoint();
        //Distance is three dimensional distance added up, so if any two = 1 then we're moving diagonally, 1 + 1 + x > 2, so if it's less than two we're moving only one block!
        return startPoint.distanceSquared(endPoint) < 2;
    }

    //Let's us identify this tile so we don't re-create it every time we need to assess a location's tile.
    public static Double getUidFor(Point point, PathTile parent) {
        return (point.getX() + point.getY() + point.getZ()) * (parent == null ? 1 : getUidFor(parent.getPoint(), parent.getParent()));
    }
}
