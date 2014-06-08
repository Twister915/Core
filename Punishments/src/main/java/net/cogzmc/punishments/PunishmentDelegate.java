package net.cogzmc.punishments;

import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.commands.PunishmentException;
import net.cogzmc.punishments.models.AbstractPunishment;

/**
 * Created by August on 5/26/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 * @author August
 */
public interface PunishmentDelegate {

	/**
	 * Gets the permission needed to create a punishment of a specific type
	 *
	 * @param cls the punishment type
	 * */
	public <T extends AbstractPunishment> String permissionFor(Class<T> cls);

	/**
	 * Gets the friendly name of a punishment type
	 *
	 * @param cls the punishment type
	 * */
	public <T extends AbstractPunishment> String nameFor(Class<T> cls);

	/**
	 * Creates a punishment of a punishment type
	 *
	 * @param cls the punishment type
	 * @param target the punishment's target player
	 * @param issuer the player that issued the punishment
	 * @param args the arguments used to in the punishment command
	 * */
	<T extends AbstractPunishment> T createPunishment(Class<T> cls, COfflinePlayer target, COfflinePlayer issuer, String[] args) throws PunishmentException;

	/**
	 * Enforces a punishment on an online player
	 *
	 * @param punishment the punishment to enforce
	 * @param target the player to punish
	 * @param issuer the punishment's issuer.
	 * */
	void enforcePunishment(AbstractPunishment punishment, CPlayer target, CPlayer issuer);
}
