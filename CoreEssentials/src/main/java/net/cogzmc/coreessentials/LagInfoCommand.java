package net.cogzmc.coreessentials;

import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;
import org.bukkit.command.CommandSender;

@CommandMeta(aliases = {"tps", "lag", "lm"}, description = "Get the current runtime info!")
@CommandPermission("core.essentials.laginfo")
public class LagInfoCommand extends ModuleCommand {
    public LagInfoCommand() {
        super("laginfo");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {

    }
}
