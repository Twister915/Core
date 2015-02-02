package net.cogzmc.punishments.types.impl.manager;

import net.cogzmc.punishments.Punishments;
import net.cogzmc.punishments.types.impl.model.Warning;

import java.util.Date;

public final class WarningManager extends BaseMongoManager<Warning> {
    public WarningManager() {
        super(Warning.class);
    }

    @Override
    Warning createNewPunishment(ObjectId objectId, COfflinePlayer target, String reason, COfflinePlayer issuer, Date issued, Boolean active) {
        return new Warning(objectId, target, reason, issuer, active, issued);
    }

    @Override
    boolean canConnect(CPlayer player, Warning punishment) {
        return true;
    }

    @Override
    void onPunish(CPlayer player, Warning punishment) {
        player.sendMessage(Core.getModule(Punishments.class).getFormat("warned", new String[]{"<issuer>", punishment.getIssuer().getName()}, new String[]{"<message>", punishment.getMessage()}));
    }

    @Override
    public void revokePunishment(Warning punishment) {
        throw new UnsupportedOperationException("You cannot revoke a warning!");
    }
}
