package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.Ban;

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
        PrettyTime formatter = new PrettyTime();
        player.kickPlayer(Core.getModule(Punishments.class).getFormat("disconnect-message-perm", false,
                new String[]{"<type>", activePunishmentFor.getClass().getSimpleName()},
                new String[]{"<reason>", activePunishmentFor.getMessage()},
                new String[]{"<issuer>", activePunishmentFor.getIssuer().getName()},
                new String[]{"<issued>", formatter.format(activePunishmentFor.getDateIssued())},
                new String[]{"<expires>", "never"}));
    }
}
