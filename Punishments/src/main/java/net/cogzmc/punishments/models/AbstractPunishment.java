package net.cogzmc.punishments.models;

import net.cogzmc.core.model.Model;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.Date;

/**
 * Created by August on 5/25/14.
 * <p/>
 * Purpose Of File: The base for punishment models
 * <p/>
 * Latest Change:
 * @author August
 */
public abstract class AbstractPunishment extends Model {

	public abstract COfflinePlayer getTarget();
	public abstract COfflinePlayer getIssuer();
	public abstract String getReason();
	public abstract Date getDate();

}