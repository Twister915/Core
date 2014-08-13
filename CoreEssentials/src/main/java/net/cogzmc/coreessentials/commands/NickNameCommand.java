package net.cogzmc.coreessentials.commands;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.*;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.coreessentials.CoreEssentials;
import org.bukkit.ChatColor;

import java.util.List;

@CommandMeta(aliases = {"nick", "nickname"})
@CommandPermission("core.essentials.nick")
public final class NickNameCommand extends ModuleCommand {
    public NickNameCommand() {
        super("nick");
    }

    @Override
    protected void handleCommand(CPlayer commandSender, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You need to specify a nickname.");
        CPlayer target = commandSender;
        String nick = args[0];
        if (args.length > 1 && commandSender.hasPermission("core.nick.others")) {
            List<CPlayer> possiblePlayers = Core.getPlayerManager().getCPlayerByStartOfName(args[0]);
            if (possiblePlayers.size() != 1) throw new ArgumentRequirementException("The player name you supplied was either not on the server, or not specific enough.");
            target = possiblePlayers.get(0);
            nick = args[1];
        }
        if (nick.length() > 16) throw new ArgumentRequirementException("The nickname you specified is too long!");
        if (nick.equalsIgnoreCase("off")) {
            nick = null;
        }
        target.setDisplayName(nick);
        target.setTagName(nick);
        String coloredNick = ChatColor.translateAlternateColorCodes('&', nick == null ? target.getName() : nick);
        CoreEssentials coreEssentials = Core.getInstance().getModuleProvider(CoreEssentials.class);
        target.sendMessage(coreEssentials.getFormat("nickname-changed", new String[]{"<name>", coloredNick}));
        if (target != commandSender) commandSender.sendMessage(coreEssentials.getFormat("nickname-changed-other", new String[]{"<target>", target.getName()}, new String[]{"<name>", coloredNick}));
        coreEssentials.getTabColorManager().updatePlayerListName(target);
    }
}
