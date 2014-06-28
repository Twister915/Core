package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.TemporaryBan;
import org.bson.types.ObjectId;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public final class TemporaryBanManager extends BaseTemporaryMongoManager<TemporaryBan> {
    public TemporaryBanManager() {
        super(TemporaryBan.class);
    }

    @Override
    TemporaryBan createNewPunishment(ObjectId id, COfflinePlayer target, String reason, COfflinePlayer issuer, Boolean active, Date issued, Integer lengthInSeconds) {
        return new TemporaryBan(id, target, reason, issuer, active, issued, lengthInSeconds);
    }

    @Override
    boolean canConnect(CPlayer player, TemporaryBan punishment) {
        return false;
    }

    @Override
    void onPunish(CPlayer player, TemporaryBan activePunishmentFor) {
        PrettyTime formatter = new PrettyTime();
        player.getBukkitPlayer().kickPlayer(Core.getModule(Punishments.class).getFormat("disconnect-message-perm", false,
                new String[]{"<type>", activePunishmentFor.getClass().getSimpleName()},
                new String[]{"<reason>", activePunishmentFor.getMessage()},
                new String[]{"<issuer>", activePunishmentFor.getIssuer().getName()},
                new String[]{"<date-issued>", formatter.format(activePunishmentFor.getDateIssued())},
                new String[]{"<expires>", "in " + formatter.format(new Date(activePunishmentFor.getDateIssued().getTime() + (activePunishmentFor.getLengthInSeconds() * 1000)))}
        ));
    }
}
