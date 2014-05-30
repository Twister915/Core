package net.communitycraft.punishments;

import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.communitycraft.punishments.commands.PunishmentException;
import net.communitycraft.punishments.models.*;

import java.util.Date;


/**
 * Created by August on 5/26/14.
 * <p/>
 * Purpose Of File: Default implementation of PunishmentDelegate
 * <p/>
 * Latest Change:
 */
public class SimplePunishmentDelegate implements PunishmentDelegate {

	String permissionNode;

	public SimplePunishmentDelegate() {
		this("punish.");
	}

	public SimplePunishmentDelegate(String permissionNode) {
		this.permissionNode = permissionNode;
	}

	@Override
	public <T extends AbstractPunishment> String permissionFor(Class<T> cls) {
		return permissionNode + nameFor(cls);
	}

	@Override
	public <T extends AbstractPunishment> String nameFor(Class<T> cls) {
		return cls.getSimpleName().toLowerCase();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AbstractPunishment> T createPunishment(Class<T> cls, COfflinePlayer target, COfflinePlayer issuer, String[] args) throws PunishmentException {
		Date date = new Date();
		if (cls == Ban.class || cls == Mute.class) {
			Long length;
			try {
				length = Long.valueOf(args[0]);
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new PunishmentException("Please supply a punishment length.");
			} catch (NumberFormatException e) {
				throw new PunishmentException("Expected a number, received a string instead.");
			}
			String reason = joinStrings(args, 1);
			return (T) (cls == Ban.class ? new Ban(reason, date, target, issuer, length) : new Mute(reason, issuer, target, date, length));
		} else {
			String reason = joinStrings(args, 0);
			if (cls == Kick.class) {
				return (T) new Kick(reason, target, issuer, date);
			} else if (cls == Warn.class) {
				return (T) new Warn(reason, target, issuer, date);
			}
		}
		return null;
	}

	@Override
	public void enforcePunishment(AbstractPunishment punishment, CPlayer target, CPlayer issuer) {
		if (punishment instanceof Ban) {
			String msg = getFormat("banned", issuer.getName(), punishment.getReason());
			target.getBukkitPlayer().kickPlayer(msg);
		} else if (punishment instanceof Kick) {
			String msg = getFormat("kicked", issuer.getName(), punishment.getReason());
			target.getBukkitPlayer().kickPlayer(msg);
		} else if (punishment instanceof Mute) {
			String msg = getFormat("muted", issuer.getName(), punishment.getReason());
			target.sendMessage(msg);
		} else if (punishment instanceof Warn) {
			String msg = getFormat("warned", issuer.getName(), punishment.getReason());
			target.sendMessage(msg);
		}
	}

	private String getFormat(String punishment, String issuer, String reason) {
		return PunishmentModule.getInstance().getFormat(punishment,
				new String[]{"<issuer>", issuer},
				new String[]{"<reason>", reason}
		);
	}

	public static String joinStrings(String[] strings, int startingIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = startingIndex; i < strings.length; i++) {
			builder.append(strings[i]).append(" ");
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}
}
