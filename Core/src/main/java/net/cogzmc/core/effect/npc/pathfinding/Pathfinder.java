package net.cogzmc.core.effect.npc.pathfinding;

import lombok.*;
import net.cogzmc.core.util.Point;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

@Data
@Setter(AccessLevel.NONE)
public class Pathfinder {
    @Getter(AccessLevel.NONE) private final Map<Double, PathTile> tilesGenerated = new HashMap<>();

    @NonNull private final Point startPos;
    @NonNull private final Point endPos;
    @NonNull private final World world;

    private final PathTile start;
    private final PathTile end;

    public Pathfinder(Point startPos, Point endPos, World world) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.world = world;

        this.start = tileFrom(startPos);
        this.end = tileFrom(endPos);
    }
    public List<PathTile> solvePath(Integer range) throws PathfindingException {
        //Create our A* sets, as defined by 99% of the documentation out there.
        Set<PathTile> closedSet = new LinkedHashSet<>(); //A closed set, to represent tiles we've used
        Set<PathTile> openSet = new LinkedHashSet<>(); //An open set, to represent tiles we intend to check
        closedSet.add(start); //Start off with our starting point
        PathTile current = start; //Setup the tracked "current" or "picked" square.
        //Iterate until we've reached the destination, or when the open set becomes empty (no spaces to move to)
        while (!current.getPoint().equals(endPos) && (openSet.size() != 0 || current == start)) { //current == start is to allow it to start with an empty open set at the start
            //Gets all tiles adjacent to us that we can move to.
            List<PathTile> tilesAdjacent = getTilesAdjacent(current);
            //Removes all the tiles that are already in our closedSet
            tilesAdjacent.removeAll(closedSet);
            openSet.addAll(tilesAdjacent);
            if (tilesAdjacent.size() == 0 && closedSet.size() == 0) throw new PathfindingException(); //May remove the second boolean, could easily be in a box and have no movement
            PathTile chosen = null;
            for (PathTile pathTile : openSet) {
                if (chosen == null) {
                    //First time setup
                    chosen = pathTile;
                    continue;
                }
                if (pathTile.getFScore() < chosen.getFScore()) chosen = pathTile;
            }
            assert chosen != null;
            current = chosen;
            openSet.remove(current);
            closedSet.add(current);
            if (range != -1 && closedSet.size() == range) break;
        }
        List<PathTile> tiles = new ArrayList<>();
        tiles.addAll(closedSet);
        Collections.reverse(tiles);
        return tiles;
    }

    public List<PathTile> solvePath() throws PathfindingException {
        return solvePath(-1);
    }

    private List<PathTile> getTilesAdjacent(PathTile tile) {
        List<PathTile> pathTiles = new ArrayList<>();
        Point center = tile.getPoint();
        //Multiple assignment FTW
        Double centerX = center.getX(), centerY = center.getY(), centerZ = center.getZ();
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                for (int z = -1; z < 1; z++) {
                    //Go through everything, skipping the center tile
                    if (x == 0 && y == 0 && z == 0) continue;
                    //Check if we can walk on the tile that we're going to add
                    Point current = Point.of(centerX + x, centerY + y, centerZ + z);
                    if (!canWalk(current)) continue;
                    PathTile pathTile = tileFrom(current, tile);
                    //update the score
                    pathTile.updateScores(start, end);
                    pathTiles.add(pathTile);
                }
            }
        }
        return pathTiles;
    }

    private boolean canWalk(Point current) {
        Block walkingOn = current.getLocation(world).getBlock(); //The block that is under the path, the thing we're standing on
        if (!isSolid(walkingOn)) return false;
        Block walkingThrough1 = walkingOn.getRelative(0, 1, 0), walkingThrough2 = walkingThrough1.getRelative(0, 1, 0); //Gets two blocks directly above the one we're walking on
        return !isSolid(walkingThrough1) && !isSolid(walkingThrough2);
    }

    private static boolean isSolid(Block b) {
        //These are non-solid blocks that you cannot stand on.
        switch (b.getType()) {
            case AIR:
            case LAVA:
            case STATIONARY_LAVA:
            case STATIONARY_WATER:
            case WATER:
            case LADDER:
            case WHEAT:
            case LONG_GRASS:
            case RAILS:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
            case CAULDRON:
            case YELLOW_FLOWER:
            case FLOWER_POT:
            case RED_ROSE:
            case CAKE_BLOCK:
            case CARPET:
                return false;
            case IRON_DOOR:
            case WOODEN_DOOR:
            case WOOD_DOOR:
            case FENCE_GATE:
                //Fourth bit in data says that we're open or closed.
                if ((b.getData() & (1<<0x4)) != 16)
                    return false;
            default:
                return true;
        }
    }

    private PathTile tileFrom(@NonNull Point point, PathTile parent) {
        Double uidFor = PathTile.getUidFor(point, parent);
        if (tilesGenerated.containsKey(uidFor)) return tilesGenerated.get(uidFor);
        PathTile pathTile = new PathTile(point, parent);
        tilesGenerated.put(uidFor, pathTile);
        return pathTile;
    }

    private PathTile tileFrom(@NonNull Point point) {
        return tileFrom(point, null);
    }

}
