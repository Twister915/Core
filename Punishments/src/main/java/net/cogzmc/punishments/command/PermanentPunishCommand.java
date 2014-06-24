package net.cogzmc.punishments.command;

import com.google.common.base.Joiner;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;
import net.cogzmc.punishments.types.PunishmentException;

import java.util.Arrays;

@CommandMeta(
        description = "Punish the target player",
        usage = "/[command] [target] [reason]"
)
public final class PermanentPunishCommand<T extends Punishment> extends BasePunishCommand<T, PunishmentManager<T>> {
    public PermanentPunishCommand(Class<T> clazz) {
        super(clazz.getSimpleName().toLowerCase(), clazz);
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {
        Punishments module = Core.getModule(Punishments.class);
        String name = clazz.getSimpleName().toLowerCase();
        super.handleCommand(sender, args);
        if (args.length < 2)
            throw new ArgumentRequirementException("You need to specify both a target and a reason to " + name + " someone!");
        String target = args[0];
        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
        COfflinePlayer targetPlayer = getTargetByArg(target);
        if (targetPlayer == null) throw new ArgumentRequirementException("The player you specified is not specific enough!");
        try {
            punishmentManager.punish(targetPlayer, reason, sender);
        } catch (PunishmentException e) {
            sender.sendMessage(module.getFormat("punishment-error", new String[]{"<error>", e.getMessage()}));
        }
        sender.sendMessage(module.getFormat("punishment-success", new String[]{"<punishment>", name}, new String[]{"<target>", targetPlayer.getName()}, new String[]{"<reason>", reason}));
    }
}
