package net.communitycraft.punishments.models;

import net.cogzmc.core.model.Model;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayerManager;

import java.util.Date;
import java.util.UUID;

/**
 * Created by August on 5/25/14.
 * <p/>
 * Purpose Of File: The base for punishment models
 * <p/>
 * Latest Change:
 */
public abstract class AbstractPunishment extends Model implements PunishmentModel {

	private static CPlayerManager defaultPlayerManager;

	public static CPlayerManager getDefaultPlayerManager() {
		return defaultPlayerManager;
	}

	public static void setDefaultPlayerManager(CPlayerManager defaultPlayerManager) {
		AbstractPunishment.defaultPlayerManager = defaultPlayerManager;
	}

	protected CPlayerManager playerManager;
	protected UUID issuer;
	protected UUID target;
	protected Date date;
	protected String reason;

	protected AbstractPunishment() {
	}

	protected AbstractPunishment(UUID issuer, UUID target, Date date, String reason) {
		this.issuer = issuer;
		this.target = target;
		this.date = date;
		this.reason = reason;
	}

	protected AbstractPunishment(COfflinePlayer issuer, COfflinePlayer target, Date date, String reason) {
		this(issuer.getUniqueIdentifier(), target.getUniqueIdentifier(), date, reason);
	}

	@Override
	public COfflinePlayer getTarget() {
		return target == null ? null : getOfflinePlayerByUUID(target);
	}

	@Override
	public String getReason() {
		return reason;
	}

	@Override
	public COfflinePlayer getIssuer() {
		return issuer == null ? null : getOfflinePlayerByUUID(issuer);
	}

	protected COfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
		if (playerManager != null) return playerManager.getOfflinePlayerByUUID(uuid);
		if (defaultPlayerManager != null) return defaultPlayerManager.getOfflinePlayerByUUID(uuid);
		return null;
	}

	public Date getDate() {
		return date;
	}

	public void setPlayerManager(CPlayerManager playerManager) {
		this.playerManager = playerManager;
	}
}