package net.cogzmc.punishments.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;

import java.util.List;
import java.util.UUID;

abstract class TargetedCommand extends ModuleCommand {
    protected TargetedCommand(String name) {
        super(name);
    }

    protected TargetedCommand(String name, ModuleCommand... subCommands) {
        super(name, subCommands);
    }

    protected COfflinePlayer getTargetByArg(String target) {
        if (target.length() > 16) {
            if (!target.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) return null;
            return Core.getPlayerManager().getOfflinePlayerByUUID(UUID.fromString(target));
        }
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
