package net.cogzmc.punishments.types.impl.model;

import net.cogzmc.punishments.types.impl.TargetOnlinesOnly;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TargetOnlinesOnly
public final class Kick extends MongoPunishment {
    public Kick(ObjectId objectId, COfflinePlayer target, String message, COfflinePlayer issuer, boolean revoked, Date dateIssued) {
        super(objectId, target, message, issuer, revoked, dateIssued);
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
