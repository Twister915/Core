package net.cogzmc.punishments.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;

import java.util.List;

abstract class TargetedCommand extends ModuleCommand {
    protected TargetedCommand(String name) {
        super(name);
    }

    protected TargetedCommand(String name, ModuleCommand... subCommands) {
        super(name, subCommands);
    }

    protected COfflinePlayer getTargetByArg(String target) {
        COfflinePlayer targetPlayer;
        List<CPlayer> possibleOnlinePlayers = Core.getPlayerManager().getCPlayerByStartOfName(target);
        if (possibleOnlinePlayers.size() != 1) {
            List<COfflinePlayer> offlinePlayers = Core.getPlayerManager().getOfflinePlayerByName(target);
            if (offlinePlayers.size() != 1) return null;
            targetPlayer = offlinePlayers.get(0);
        } else {
            targetPlayer = possibleOnlinePlayers.get(0);
        }
        return targetPlayer;
    }
}
