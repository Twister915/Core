package net.communitycraft.punishments;

import net.communitycraft.punishments.models.Mute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

/**
 * Created by August on 6/1/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 *
 * Latest Change:
 * @author August
 */
public interface MuteDelegate {

	/**
	 * Whether or not a player can chat
	 *
	 * @param player The player's UUID
	 * */
	boolean canChat(UUID player);

	/**
	 * Mutes a player with a Mute punishment
	 *
	 * @param player The player's UUID
	 * @param mute The Mute punishment
	 * */
	void mutePlayer(UUID player, Mute mute);

	/**
	 * Unmutes a player
	 *
	 * @param player The player's UUID
	 * */
	void unmutePlayer(UUID player);

	/**
	 * Gets the mute, if any, corresponding to the player
	 *
	 * @param player The player's UUID
	 * */
	Mute getMute(UUID player);

	public static class Handler implements Listener {
		private MuteDelegate delegate;

		public Handler(MuteDelegate delegate) {
			this.delegate = delegate;
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void onChat(AsyncPlayerChatEvent event) {
			if(!delegate.canChat(event.getPlayer().getUniqueId())) {
				String msg = PunishmentModule.getInstance().getFormat("muted-chat");
				event.getPlayer().sendMessage(msg);
				event.setCancelled(true);
			}
		}
	}
}
