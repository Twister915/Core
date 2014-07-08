package net.cogzmc.core.test;

import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;

@CommandPermission("tests.use")
public final class TestsCommand extends ModuleCommand {
    public TestsCommand() {
        super("tests");
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
