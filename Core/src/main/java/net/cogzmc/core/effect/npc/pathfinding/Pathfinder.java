package net.cogzmc.core.effect.npc.pathfinding;

import lombok.Data;
import lombok.NonNull;
import net.cogzmc.core.util.Point;

import java.util.Collections;
import java.util.List;

@Data
public final class Pathfinder {
    @NonNull private final Point start;
    @NonNull private final Point end;

    public List<PathTile> solvePath(Integer range) {
        return Collections.emptyList();
    }
}
