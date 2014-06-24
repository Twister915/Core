package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.core.Core;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.Ban;
import org.bson.types.ObjectId;

import java.util.Date;

public final class BanManager extends BaseMongoManager<Ban> {
    public BanManager() {
        super(Ban.class);
    }

    @Override
    Ban createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active) {
        return new Ban(objectId, target, reason, issuer, active, issued);
    }

    @Override
    boolean canConnect(CPlayer player, Ban punishment) {
        return false;
    }

    @Override
    void onPunish(CPlayer player, Ban activePunishmentFor) {
        player.getBukkitPlayer().kickPlayer(Core.getModule(Punishments.class).getFormat("disconnect-message-perm", false,
                new String[]{"<type>", activePunishmentFor.getClass().getSimpleName()},
                new String[]{"<reason>", activePunishmentFor.getMessage()},
                new String[]{"<issuer>", activePunishmentFor.getIssuer().getName()},
                new String[]{"<date-issued>", PRETTY_TIME_FORMATTER.format(activePunishmentFor.getDateIssued())}));
    }
}
