package net.communitycraft.punishments.models;

/**
 * Created by August on 6/1/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 * @author August
 */
public interface ExpirablePunishment {

	/**
	 * Gets the length of the punishment
	 * */
	Long getLength();

	/**
	 * Whether or not the punishment has expired
	 * */
	boolean isExpired();

}
