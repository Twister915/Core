package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.Kick;

import java.util.Date;

public final class KickManager extends BaseMongoManager<Kick> {
    public KickManager() {
        super(Kick.class);
    }

    @Override
    Kick createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active) {
        return new Kick(objectId, target, reason, issuer, active, issued);
    }

    @Override
    boolean canConnect(CPlayer player, Kick punishment) {
        return true;
    }

    @Override
    public void revokePunishment(Kick punishment) {
        throw new UnsupportedOperationException("You cannot revoke a kick!");
    }

    @Override
    void onPunish(CPlayer player, Kick punishment) {
        player.kickPlayer(Core.getModule(Punishments.class).getFormat("kick", false, new String[]{"<issuer>", punishment.getIssuer().getName()}, new String[]{"<reason>", punishment.getMessage()}));
    }
}
