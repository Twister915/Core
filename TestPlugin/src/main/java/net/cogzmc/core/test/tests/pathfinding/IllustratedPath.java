package net.cogzmc.core.test.tests.pathfinding;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cogzmc.core.effect.npc.pathfinding.PathTile;
import net.cogzmc.core.effect.npc.pathfinding.Pathfinder;
import net.cogzmc.core.effect.npc.pathfinding.PathfindingException;
import net.cogzmc.core.util.Point;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

@Data
@Setter(AccessLevel.NONE)
public final class IllustratedPath {
    private final World world;
    private final Point start;
    private final Point end;

    private Map<Point, Material> previouslyIllustratedBlocks = new HashMap<>();

    public void illustratePath() throws PathfindingException {
        Pathfinder pathfinder = new Pathfinder(start, end, world);
        cleanupLastIllustration();
        for (PathTile pathTile : pathfinder.solvePath(500)) {
            Block block = pathTile.getPoint().getLocation(world).getBlock();
            previouslyIllustratedBlocks.put(pathTile.getPoint(), block.getType());
            if (pathTile.equals(pathfinder.getStart())) block.setType(Material.EMERALD_BLOCK);
            else if (pathTile.equals(pathfinder.getEnd())) block.setType(Material.REDSTONE_BLOCK);
            else block.setType(Material.DIAMOND_BLOCK);
        }
    }

    public void cleanupLastIllustration() {
        for (Map.Entry<Point, Material> pointMaterialEntry : previouslyIllustratedBlocks.entrySet()) {
            pointMaterialEntry.getKey().getLocation(world).getBlock().setType(pointMaterialEntry.getValue());
        }
        previouslyIllustratedBlocks = new HashMap<>();
    }
}
