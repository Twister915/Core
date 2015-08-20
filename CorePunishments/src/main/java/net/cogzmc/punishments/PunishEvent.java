package net.cogzmc.punishments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.punishments.types.Punishment;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = true)
@Data
public final class PunishEvent extends Event implements Cancellable {
    @SuppressWarnings("unused")
    private static final HandlerList handlerList = new HandlerList();

    private final COfflinePlayer target, punisher;
    private final Punishment punishment;
    private boolean cancelled;

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
