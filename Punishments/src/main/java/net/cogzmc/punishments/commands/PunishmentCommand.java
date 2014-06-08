package net.cogzmc.punishments.commands;

import net.cogzmc.core.Core;
import net.cogzmc.core.modular.command.ArgumentRequirementException;
import net.cogzmc.core.modular.command.CommandException;
import net.cogzmc.core.modular.command.ModuleCommand;
import net.cogzmc.core.modular.command.PermissionException;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.PunishmentDelegate;
import net.cogzmc.punishments.PunishmentModule;
import net.cogzmc.punishments.models.AbstractPunishment;

import java.util.List;

/**
 * Created by August on 5/26/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 * @author August
 */
public class PunishmentCommand extends ModuleCommand {

	Class<? extends AbstractPunishment> punishClass;
	PunishmentDelegate delegate;

	public PunishmentCommand(Class<? extends AbstractPunishment> punishClass, PunishmentDelegate delegate) {
		super(delegate.nameFor(punishClass));
		this.punishClass = punishClass;
		this.delegate = delegate;
	}

	@Override
	protected void handleCommand(CPlayer player, String[] args) throws CommandException {
		if (!player.hasPermission(delegate.permissionFor(punishClass)))
			throw new PermissionException(PunishmentModule.getInstance().getFormat("no-permission"));
		if (args.length < 1) throw new ArgumentRequirementException("Please supply a username");
		COfflinePlayer target = Core.getPlayerManager().getOnlineCPlayerForName(args[0]);
		if (target == null) {
			List<COfflinePlayer> players = Core.getPlayerManager().getOfflinePlayerByName(args[0]);
			if (players.isEmpty()) throw new CommandException("Player not found!");
			target = players.get(0);
		}
		AbstractPunishment punishment = delegate.createPunishment(punishClass, target, player, shiftArgs(args, 1));
		if (target instanceof CPlayer) { // Only if target is online
			delegate.enforcePunishment(punishment, (CPlayer) target, player);
		}
	}

	public static String[] shiftArgs(String[] original, int amount) {
		String[] shifted = new String[original.length - amount];
		System.arraycopy(original, amount, shifted, amount - amount, original.length - amount);
		return shifted;
	}

}
