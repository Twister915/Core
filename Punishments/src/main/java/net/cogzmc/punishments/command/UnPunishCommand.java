package net.cogzmc.punishments.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.modular.command.PermissionException;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;
import org.bukkit.command.CommandSender;

@CommandMeta(
        description = "Un-punish the specified player!",
        usage = "/un[punishment] [target]"
)
public final class UnPunishCommand<T extends Punishment> extends BasePunishCommand<T, PunishmentManager<T>> {
    public UnPunishCommand(Class<T> clazz) {
        super("un" + clazz.getSimpleName().toLowerCase(), clazz);
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        String name = clazz.getSimpleName().toLowerCase();
        if (!sender.hasPermission("punish." + name)) throw new PermissionException("You do not have permission to " + name + " people!");
        if (args.length < 1) throw new ArgumentRequirementException("You have not specified a player!");
        COfflinePlayer targetByArg = getTargetByArg(args[0]);
        if (targetByArg == null) throw new ArgumentRequirementException("The player specified is not specific enough!");
        T activePunishmentFor = punishmentManager.getActivePunishmentFor(targetByArg);
        if (activePunishmentFor == null) throw new ArgumentRequirementException("This user has no punishment!");
        punishmentManager.revokePunishment(activePunishmentFor);
        sender.sendMessage(Core.getModule(Punishments.class).getFormat("unpunish-success", new String[]{"<target>", targetByArg.getName()}, new String[]{"<punishment>", clazz.getSimpleName().toLowerCase()}, new String[]{"<reason>", activePunishmentFor.getMessage()}));
    }
}
