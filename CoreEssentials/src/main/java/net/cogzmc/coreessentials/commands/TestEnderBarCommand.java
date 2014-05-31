package net.cogzmc.coreessentials.commands;

import com.google.common.base.Joiner;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;

import java.util.Random;

public final class TestEnderBarCommand extends ModuleCommand {
    public TestEnderBarCommand() {
        super("testenderbar");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        String join = args.length < 1 ? "Testing!" : Joiner.on(" ").join(args);
        Core.getEnderBarManager().setTextFor(player, join);
        Random random = new Random();
        Core.getEnderBarManager().setHealthPercentageFor(player, random.nextFloat());
    }
}
