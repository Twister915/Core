package net.cogzmc.punishments.types.impl.model;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public final class IPBan extends MongoPunishment {
    public IPBan(ObjectId mongoId, COfflinePlayer target, String message, COfflinePlayer issuer, boolean active, Date dateIssued) {
        super(mongoId, target, message, issuer, active, dateIssued);
    }
}
