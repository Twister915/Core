package net.cogzmc.punishments.command;

import com.google.common.base.Joiner;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.CommandMeta;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.util.TimeUtils;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.TimedPunishmentManager;
import net.cogzmc.punishments.types.PunishmentException;
import net.cogzmc.punishments.types.TimedPunishment;

import java.util.Arrays;

@CommandMeta(
        description = "Punish the target player for a specified amount of time",
        usage = "/[command] [target] [time] [reason]"
)
public final class TemporaryPunishCommand<T extends TimedPunishment> extends BasePunishCommand<T, TimedPunishmentManager<T>> {
    public TemporaryPunishCommand(Class<T> clazz) {
        super(Punishments.getNameFor(clazz), clazz);
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {
        Punishments module = Core.getModule(Punishments.class);
        super.handleCommand(sender, args);
        if (args.length < 3)
            throw new ArgumentRequirementException("You must specify a target, length, and reason to " + name + " someone!");
        String target = args[0];
        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length));
        Integer seconds = TimeUtils.parseTime(args[1]).intValue();
        if (seconds == 0) throw new ArgumentRequirementException("The time you specified is invalid!");
        COfflinePlayer targetByArg = getTargetByArg(target);
        if (targetByArg == null) throw new ArgumentRequirementException("You have specified a player that is not specific enough!");
        try {
            punishmentManager.punish(targetByArg, reason, sender, seconds);
        } catch (PunishmentException e) {
            sender.sendMessage(module.getFormat("punishment-error", new String[]{"<error>", e.getMessage()}));
        }
        sender.sendMessage(module.getFormat("punishment-success", new String[]{"<punishment>", name}, new String[]{"<target>", targetByArg.getName()}, new String[]{"<reason>", reason}));
    }
}
