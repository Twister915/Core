package net.cogzmc.coreessentials;

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
        Core.getEnderBarManager().setTextFor(player, "Testing 123!");
        Random random = new Random();
        Core.getEnderBarManager().setHealthPercentageFor(player, random.nextFloat());
    }
}
