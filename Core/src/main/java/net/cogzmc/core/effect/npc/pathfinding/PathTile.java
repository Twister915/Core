package net.cogzmc.core.effect.npc.pathfinding;

import lombok.Data;
import lombok.NonNull;
import net.cogzmc.core.util.Point;

@Data
public final class PathTile {
    @NonNull private final Point point;
    private final PathTile parent;
}
