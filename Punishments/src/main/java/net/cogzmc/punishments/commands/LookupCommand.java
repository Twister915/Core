package net.cogzmc.punishments.commands;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.PunishmentModule;
import net.cogzmc.punishments.models.AbstractPunishment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by August on 6/1/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class LookupCommand extends ModuleCommand {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");

	PunishmentManager punishmentManager;

	public LookupCommand(PunishmentManager punishmentManager) {
		super("lookup");
		this.punishmentManager = punishmentManager;
	}

	@Override
	protected void handleCommand(CPlayer player, String[] args) throws CommandException {
		if(args.length < 1) throw new ArgumentRequirementException("Too few arguments.");
		String username = args[0];
		List<COfflinePlayer> players = Core.getPlayerManager().getOfflinePlayerByName(username);
		if(players.isEmpty()) throw new CommandException("Player not found: " + username);
		COfflinePlayer target = players.get(0);
		List<String> punishmentMessages = new ArrayList<>();
		for(AbstractPunishment punishment : punishmentManager.findReceivedPunishments(target)) {
			punishmentMessages.add(getFormat(punishment));
		}
		if(punishmentMessages.isEmpty()) {
			player.sendMessage(PunishmentModule.getInstance().getFormat("lookup-clean", new String[]{"<target>", target.getName()}));
		} else {
			for(String msg : punishmentMessages) {
				player.sendMessage(msg);
			}
		}
	}

	private String getFormat(AbstractPunishment punishment) {
		return PunishmentModule.getInstance().getFormat("lookup-item",
				new String[]{"<punishment>", punishmentManager.getDelegate().nameFor(punishment.getClass())},
				new String[]{"<issuer>", punishment.getIssuer().getName()},
				new String[]{"<target>", punishment.getTarget().getName()},
				new String[]{"<reason>", punishment.getReason()},
				new String[]{"<date>", dateFormat.format(punishment.getDate())}
		);
	}

}
