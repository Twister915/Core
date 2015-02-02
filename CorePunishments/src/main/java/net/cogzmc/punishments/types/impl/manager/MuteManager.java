package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.Mute;
import org.bson.types.ObjectId;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class MuteManager extends BaseMongoManager<Mute> implements Listener {
    private final Set<CPlayer> mutedPlayers = new HashSet<>();
    private final Punishments module = Core.getModule(Punishments.class);

    public MuteManager() {
        super(Mute.class);
        Core.getModule(Punishments.class).registerListener(this);
    }

    @Override
    Mute createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active) {
        return new Mute(objectId, target, reason, issuer, active, issued);
    }

    @Override
    void onPunish(CPlayer player, Mute punishment) {
        mutedPlayers.add(player);
        player.sendMessage(module.getFormat("new-mute",
                new String[]{"<reason>", punishment.getMessage()},
                new String[]{"<issuer>", punishment.getIssuer().getName()},
                new String[]{"<expires>", "never"}));
    }

    @Override
    boolean canConnect(CPlayer player, Mute punishment) {
        return true;
    }

    @Override
    void onJoin(CPlayer player, Mute punishment) {
        player.sendMessage(Core.getModule(Punishments.class).getFormat("muted", new String[]{"<expires>", "never"}));
        mutedPlayers.add(player);
    }

    @Override
    void onLeave(CPlayer player, Mute punishment) {
        mutedPlayers.remove(player);
    }

    @Override
    void onRevoke(CPlayer player, Mute punishment) {
        mutedPlayers.remove(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!mutedPlayers.contains(Core.getOnlinePlayer(event.getPlayer()))) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(module.getFormat("muted", new String[]{"<expires>", "never"}));
    }
}
