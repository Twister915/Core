package net.cogzmc.coreessentials.commands;

import com.google.common.base.Joiner;
import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.*;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.coreessentials.CoreEssentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

@CommandMeta(description = "Lets you generate a report on a player.", usage = "/playerreport [name]", aliases = {"whois", "playerinfo", "who"})
@CommandPermission("core.essentials.lookup")
public final class PlayerReportCommand extends ModuleCommand {
    public PlayerReportCommand() {
        super("playerreport");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) throw new ArgumentRequirementException("You must specify a player!");
        COfflinePlayer targetedPlayer = getTargetedPlayer(args[0]);
        if (targetedPlayer == null) throw new ArgumentRequirementException("The player you specified does not exist or is not specific enough!");
        sender.sendMessage(CoreEssentials.getInstance().getFormat("lookup-header", false, new String[]{"<name>"}));
        PrettyTime prettyTime = new PrettyTime();
        sender.sendMessage(getFormattedStat("UUID", targetedPlayer.getUniqueIdentifier().toString()));
        sender.sendMessage(getFormattedStat("IPs", Joiner.on(", ").join(targetedPlayer.getKnownIPAddresses())));
        sender.sendMessage(getFormattedStat("Usernames", Joiner.on(", ").join(targetedPlayer.getKnownUsernames())));
        sender.sendMessage(getFormattedStat("Last Time Seen", prettyTime.format(targetedPlayer.getLastTimeOnline())));
        sender.sendMessage(getFormattedStat("First Time Joined", prettyTime.format(targetedPlayer.getFirstTimeOnline())));
        String timeOnlineFormat = new PrettyTime(new Date(0)).format(new Date(targetedPlayer.getMillisecondsOnline()));
        sender.sendMessage(getFormattedStat("Time Spent Online", timeOnlineFormat));
        sender.sendMessage(getFormattedStat("Display Name", ChatColor.translateAlternateColorCodes('&', targetedPlayer.getDisplayName())));
    }

    private String getFormattedStat(String name, String value) {
        return " " + CoreEssentials.getInstance().getFormat("lookup-line", false, new String[]{"<name>", name}, new String[]{"<value>", value});
    }

    private COfflinePlayer getTargetedPlayer(String arg) {
        List<CPlayer> cPlayerByStartOfName = Core.getPlayerManager().getCPlayerByStartOfName(arg);
        if (cPlayerByStartOfName.size() == 1) return cPlayerByStartOfName.get(0);
        List<COfflinePlayer> offlinePlayerByName = Core.getPlayerManager().getOfflinePlayerByName(arg);
        if (offlinePlayerByName.size() == 1) return offlinePlayerByName.get(0);
        return null;
    }
}
