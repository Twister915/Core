package net.cogzmc.coreessentials.commands;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.CommandPermission;
import net.cogzmc.core.modular.command.ModuleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

@CommandMeta(aliases = {"list", "who"})
@CommandPermission("core.essentials.list")
public final class ListCommand extends ModuleCommand {
    public ListCommand() {
        super("corelist");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        String solidLine = ChatColor.STRIKETHROUGH + getTopLine();
        if (Core.getNetworkManager() == null) throw new CommandException("No network manager provided!");

    }

    public String getTopLine() {
        char[] line = new char[52];
        Arrays.fill(line, ' ');
        return new String(line);
    }
}
