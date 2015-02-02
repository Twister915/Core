package net.cogzmc.punishments.types.impl.model;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public final class Ban extends MongoPunishment {
    public Ban(ObjectId objectId, COfflinePlayer target, String message, COfflinePlayer issuer, boolean revoked, Date dateIssued) {
        super(objectId, target, message, issuer, revoked, dateIssued);
    }
}
