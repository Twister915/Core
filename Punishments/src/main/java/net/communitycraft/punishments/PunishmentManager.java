package net.communitycraft.punishments;

import lombok.Getter;
import net.cogzmc.core.Core;
import net.cogzmc.core.model.ModelStorage;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.communitycraft.punishments.models.*;
import org.bukkit.ChatColor;

import java.net.InetAddress;
import java.util.*;

/**
 * Created by August on 5/25/14.
 *
 * Purpose Of File: To provide a way to query for punishments across all model types
 *
 * Latest Change:
 */
public class PunishmentManager implements CPlayerConnectionListener {

	// List of all punishment model classes to load into the storageResolver
	public static final List<Class<? extends AbstractPunishment>> PUNISHMENT_CLASSES = Arrays.asList(Ban.class, Kick.class, Mute.class, Warn.class);

	// Any entry placed into the map should have identical types for the Class and ModelStorage
	private final Map<Class<? extends AbstractPunishment>, ModelStorage<? extends AbstractPunishment>> storageResolver = new HashMap<>();
	@Getter private final PunishmentDelegate delegate;

	public PunishmentManager() {
		for (Class<? extends AbstractPunishment> cls : PUNISHMENT_CLASSES) {
			storageResolver.put(cls, Core.getModelManager().getModelStorage(cls));
		}
		delegate = new SimplePunishmentDelegate();
	}

	@Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
		List<Ban> bans = findReceivedPunishments(player, Ban.class);
		if (!bans.isEmpty()) {
			Ban ban = bans.get(0);
			// TODO: Use formats
			player.getBukkitPlayer().kickPlayer(ChatColor.RED + "You have been banned for: " + ban.getReason());
		}
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
    }

	/**
	 * Gets the ModelStorage for the given model class
	 * @param cls the punishment model class
	 */
	@SuppressWarnings("unchecked")
		public <T extends AbstractPunishment> ModelStorage<T> storageFor(Class<T> cls) {
		ModelStorage<? extends AbstractPunishment> storage = storageResolver.get(cls);
		if (storage == null) throw new IllegalArgumentException("Could not find ModelStorage for punishment class " + cls.getClass().getSimpleName());
		return (ModelStorage<T>) storage;
	}

	private <T extends AbstractPunishment> void addPunishmentStorage(Class<T> cls, ModelStorage<T> storage) {
		storageResolver.put(cls, storage);
	}

	/**
	 * Returns all punishments matching the query criteria
	 * @param key the model's key
	 * @param val the value associated with the key
	 * */
	public List<AbstractPunishment> findPunishments(String key, Object val) {
		List<AbstractPunishment> punishments = new ArrayList<>();
		for (Class<? extends AbstractPunishment> cls : storageResolver.keySet()) {
			punishments.addAll(storageFor(cls).findValues(key, val));
		}
		return punishments;
	}

	/**
	 * Returns all punishments matching the query criteria and punishment type
	 * @param key the model's key
	 * @param val the value associated with the key
	 * @param cls the punishment model class
	 * */
	public <T extends AbstractPunishment> List<T> findPunishments(String key, Object val, Class<T> cls) {
		return storageFor(cls).findValues(key, val);
	}

	/**
	 * Returns one punishment matching the query criteria
	 * @param key the model's key
	 * @param val the value associated with the key
	 * */
	public AbstractPunishment findPunishment(String key, Object val) {
		AbstractPunishment punishment = null;
		for (Class<? extends AbstractPunishment> cls : storageResolver.keySet()) {
			AbstractPunishment result = storageFor(cls).findValue(key, val);
			if (result != null) punishment = result;
		}
		return punishment;
	}

	/**
	 * Returns one punishment matching the query criteria and punishment type
	 * @param key the model's key
	 * @param val the value associated with the key
	 * @param cls the punishment model class
	 * */
	public <T extends AbstractPunishment> T findPunishment(String key, Object val, Class<T> cls) {
		return storageFor(cls).findValue(key, val);
	}

	/**
	 * Finds all punishments received by the player
	 * @param player the player to find received punishments for
	 * */
	public List<AbstractPunishment> findReceivedPunishments(COfflinePlayer player) {
		return findPunishments("target", player);
	}

	/**
	 * Finds all punishments received by the player of a specific punishment type
	 * @param player the player to find received punishments for
	 * @param cls the punishment model class
	 * */
	public <T extends AbstractPunishment> List<T> findReceivedPunishments(COfflinePlayer player, Class<T> cls) {
		return findPunishments("target", player, cls);
	}

	/**
	 * Finds all punishments issued by the player
	 * @param player the player to find issued punishments for
	 * */
	public List<AbstractPunishment> findIssuedPunishments(COfflinePlayer player) {
		return findPunishments("issuer", player);
	}

	/**
	 * Finds all punishments issued by the player of a specific punishment type
	 * @param player the player to find issued punishments for
	 * @param cls the punishment model class
	 * */
	public <T extends AbstractPunishment> List<T> findIssuedPunishments(COfflinePlayer player, Class<T> cls) {
		return findPunishments("issuer", player, cls);
	}

}
