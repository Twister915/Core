package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.TemporaryMute;
import org.bson.types.ObjectId;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class TemporaryMuteManager extends BaseTemporaryMongoManager<TemporaryMute> implements Listener {
    private final Set<CPlayer> mutedPlayers = new HashSet<>();
    private final Punishments module = Core.getModule(Punishments.class);

    public TemporaryMuteManager() {
        super(TemporaryMute.class);
        Core.getModule(Punishments.class).registerListener(this);
    }

    @Override
    TemporaryMute createNewPunishment(ObjectId id, COfflinePlayer target, String reason, COfflinePlayer issuer, Boolean active, Date issued, Integer lengthInSeconds) {
        return new TemporaryMute(id, target, reason, issuer, active, issued, lengthInSeconds);
    }

    @Override
    void onPunish(CPlayer player, TemporaryMute punishment) {
        mutedPlayers.add(player);
    }

    @Override
    boolean canConnect(CPlayer player, TemporaryMute punishment) {
        return true;
    }


    @Override
    void onJoin(CPlayer player, TemporaryMute punishment) {
        PrettyTime formatter = new PrettyTime();
        player.sendMessage(module.getFormat("new-mute",
                new String[]{"<reason>", punishment.getMessage()},
                new String[]{"<issuer>", punishment.getIssuer().getName()},
                new String[]{"<expires>",
                        formatter.format(new Date(punishment.getLengthInSeconds()*1000 + punishment.getDateIssued().getTime()))}
        ));
        mutedPlayers.add(player);
    }

    @Override
    void onLeave(CPlayer player, TemporaryMute punishment) {
        mutedPlayers.remove(player);
    }

    @Override
    void onRevoke(CPlayer player, TemporaryMute punishment) {
        mutedPlayers.remove(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!mutedPlayers.contains(Core.getOnlinePlayer(event.getPlayer()))) return;
        event.setCancelled(true);
        PrettyTime formatter = new PrettyTime();
        TemporaryMute punishment = getActivePunishmentFor(Core.getOnlinePlayer(event.getPlayer()));
        event.getPlayer().sendMessage(module.getFormat("muted", new String[]{"<expires>", formatter.format(new Date(punishment.getLengthInSeconds() * 1000 + punishment.getDateIssued().getTime()))}));
    }
}
