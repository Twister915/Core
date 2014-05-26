package net.cogzmc.coreessentials;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.*;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.util.List;

@CommandMeta(aliases = {"nick", "nickname"})
@CommandPermission("core.nick")
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
        String coloredNick = ChatColor.translateAlternateColorCodes('&', nick == null ? target.getName() : nick);
        CoreEssentials moduleProvider = Core.getInstance().getModuleProvider(CoreEssentials.class);
        target.sendMessage(moduleProvider.getFormat("nickname-changed", new String[]{"<name>", coloredNick}));
        if (target != commandSender) commandSender.sendMessage(moduleProvider.getFormat("nickname-change-other", new String[]{"<target>", target.getName()}, new String[]{"<name>", coloredNick}));
        moduleProvider.getTabColorManager().updatePlayerListName(target);
    }
}
