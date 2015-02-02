package net.cogzmc.punishments.command;

import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.Punishment;
import net.cogzmc.punishments.types.TimedPunishment;

import java.util.*;

public final class LookupCommand extends TargetedCommand {
    public LookupCommand() {
        super("lookup");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Punishments punishmentsModule = Core.getModule(Punishments.class);
        if (!sender.hasPermission("punish.lookup")) throw new PermissionException("You do not have permission for this command!");
        if (args.length < 1) throw new ArgumentRequirementException("You have not specified a target!");
        COfflinePlayer player = getTargetByArg(args[0]);
        if (player == null) throw new ArgumentRequirementException("The player you specified is not specific enough!");
        List<Punishment> punishments = new ArrayList<>();
        for (PunishmentManager<?> punishmentManager : Core.getModule(Punishments.class).getPunishmentManagers()) {
            for (Punishment punishment : punishmentManager.getPunishmentsFor(player)) {
                punishments.add(punishment);
            }
        }
        Collections.sort(punishments, new Comparator<Punishment>() {
            @Override
            public int compare(Punishment o1, Punishment o2) {
                return (int) (o2.getDateIssued().getTime()-o1.getDateIssued().getTime());
            }
        });
        sender.sendMessage(punishmentsModule.getFormat("lookup-top-line", false, new String[]{"<count>", String.valueOf(punishments.size())}, new String[]{"<target>", player.getName()}));
        PrettyTime prettyTime = new PrettyTime();
        for (int x = 0; x < punishments.size(); x++) {
            Punishment punishment = punishments.get(x);
            StringBuilder nameBuilder = new StringBuilder(Punishments.getNameFor(punishment.getClass()));
            nameBuilder.setCharAt(0,Character.toUpperCase(nameBuilder.charAt(0)));
            String dateIssued = prettyTime.format(punishment.getDateIssued());
            String dateExpires = (punishment instanceof TimedPunishment) ? prettyTime.format(new Date(punishment.getDateIssued().getTime() + ((TimedPunishment) punishment).getLengthInSeconds()*1000)) : "never";
            sender.sendMessage(punishmentsModule.getFormat("lookup-punishment", false,
                    new String[]{"<active>", punishment.isActive() ? "yes" : "no"},
                    new String[]{"<type>", nameBuilder.toString()},
                    new String[]{"<issuer>", punishment.getIssuer().getName()},
                    new String[]{"<expires>", dateExpires},
                    new String[]{"<issued>", dateIssued},
                    new String[]{"<reason>", punishment.getMessage()},
                    new String[]{"<index>", String.valueOf(x+1)}));
        }
    }
}
