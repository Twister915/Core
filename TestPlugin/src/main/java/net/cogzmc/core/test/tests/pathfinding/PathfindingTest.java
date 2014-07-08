package net.cogzmc.core.test.tests.pathfinding;

import net.cogzmc.core.effect.npc.pathfinding.PathfindingException;
import net.cogzmc.core.test.TestModule;
import net.cogzmc.core.test.tests.ITest;

import java.util.ArrayList;
import java.util.List;

public final class PathfindingTest implements ITest {
    private final List<IllustratedPath> paths = new ArrayList<>();

    @Override
    public void onEnable() {
        TestModule.getInstance().getCommand().registerSubCommand(new PathfindingTestCommand(this));
    }

    @Override
    public void onDisable() {
        for (IllustratedPath path : paths) {
            path.cleanupLastIllustration();
        }
    }

    void handleNewPath(IllustratedPath path) throws PathfindingException {
        this.paths.add(path);
        path.illustratePath();
    }

    public void updateAll() {
        for (IllustratedPath path : this.paths) {
            try {
                path.illustratePath();
            } catch (PathfindingException e) {
                e.printStackTrace();
            }
        }

    }
}
