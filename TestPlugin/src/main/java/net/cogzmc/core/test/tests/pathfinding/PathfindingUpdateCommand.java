package net.cogzmc.core.test.tests.pathfinding;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

public final class PathfindingUpdateCommand extends ModuleCommand {
    private final PathfindingTest test;

    public PathfindingUpdateCommand(PathfindingTest test) {
        super("update");
        this.test = test;
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        this.test.updateAll();
    }
}
