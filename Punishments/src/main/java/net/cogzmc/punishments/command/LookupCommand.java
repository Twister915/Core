package net.cogzmc.punishments.command;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.PermissionException;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;
import net.cogzmc.punishments.types.TimedPunishment;
import org.bukkit.command.CommandSender;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;

public final class LookupCommand extends TargetedCommand {
    static PrettyTime PRETTY_TIME_FORMATTER = new PrettyTime();
    public LookupCommand() {
        super("lookup");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Punishments punishmentsModule = Core.getModule(Punishments.class);
        if (!sender.hasPermission("punish.lookup")) throw new PermissionException("You do not have permission for this command!");
        if (args.length < 1) throw new ArgumentRequirementException("You have not specified a target!");
        COfflinePlayer player = getTargetByArg(args[0]);
        List<Punishment> punishments = new ArrayList<>();
        for (PunishmentManager<?> punishmentManager : Core.getModule(Punishments.class).getPunishmentManagers()) {
            for (Punishment punishment : punishmentManager.getPunishmentsFor(player)) {
                punishments.add(punishment);
            }
        }
        Collections.sort(punishments, new Comparator<Punishment>() {
            @Override
            public int compare(Punishment o1, Punishment o2) {
                return (int) (o1.getDateIssued().getTime()-o2.getDateIssued().getTime());
            }
        });
        sender.sendMessage(punishmentsModule.getFormat("lookup-top-line", false, new String[]{"<count>", String.valueOf(punishments.size())}, new String[]{"<target>", player.getName()}));
        for (Punishment punishment : punishments) {
            String dateIssued = PRETTY_TIME_FORMATTER.format(punishment.getDateIssued());
            String dateExpires = (punishment instanceof TimedPunishment) ? PRETTY_TIME_FORMATTER.format(new Date(punishment.getDateIssued().getTime() + ((TimedPunishment) punishment).getLengthInSeconds()*1000)) : "never";
            sender.sendMessage(punishmentsModule.getFormat("lookup-punishment", false,
                    new String[]{"<active>", punishment.isActive() ? "yes" : "no"},
                    new String[]{"<type>", Punishments.getNameFor(punishment.getClass())},
                    new String[]{"<issuer>", punishment.getIssuer().getName()},
                    new String[]{"<expires>", dateExpires},
                    new String[]{"<issued>", dateIssued}));
        }
    }
}
