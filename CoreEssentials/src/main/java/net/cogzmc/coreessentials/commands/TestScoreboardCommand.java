package net.cogzmc.coreessentials.commands;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.scoreboard.ScoreboardAttachment;

@CommandPermission("core.tests")
public final class TestScoreboardCommand extends ModuleCommand {
    public TestScoreboardCommand() {
        super("testscoreboard");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ScoreboardAttachment scoreboardAttachment = player.getScoreboardAttachment();
        scoreboardAttachment.setPrefix("Testing");
        scoreboardAttachment.setSideTitle("Test Title");
        scoreboardAttachment.setSideText("test", 1);
        scoreboardAttachment.setSideText("test2", 2);
        scoreboardAttachment.setSideText("test4", 3);
    }
}
