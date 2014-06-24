package net.cogzmc.punishments.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;

@CommandMeta(
        description = "Un-punish the specfied player!",
        usage = "/un[punishment] [target]"
)
public final class UnPunishCommand<T extends Punishment> extends BasePunishCommand<T, PunishmentManager<T>> {
    public UnPunishCommand(Class<T> clazz) {
        super("un" + clazz.getSimpleName().toLowerCase(), clazz);
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {
        super.handleCommand(sender, args);
        if (args.length < 1) throw new ArgumentRequirementException("You have not specified a player!");
        COfflinePlayer targetByArg = getTargetByArg(args[0]);
        if (targetByArg == null) throw new ArgumentRequirementException("The player specified is not specific enough!");
        T activePunishmentFor = punishmentManager.getActivePunishmentFor(targetByArg);
        if (activePunishmentFor == null) throw new ArgumentRequirementException("This user has no punishment!");
        punishmentManager.revokePunishment(activePunishmentFor);
        sender.sendMessage(Core.getModule(Punishments.class).getFormat("unpunish-success", new String[]{"<target>", targetByArg.getName()}, new String[]{"<punishment>", clazz.getSimpleName().toLowerCase()}));
    }
}
