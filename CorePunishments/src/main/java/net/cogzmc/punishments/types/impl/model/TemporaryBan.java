package net.cogzmc.punishments.types.impl.model;

import net.cogzmc.punishments.types.HumanFriendlyName;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@HumanFriendlyName("TempBan")
public final class TemporaryBan extends MongoTemporaryPunishment {
    public TemporaryBan(ObjectId id, COfflinePlayer target, String message, COfflinePlayer issuer, boolean revoked, Date dateIssued, Integer lengthInSeconds) {
        super(id, target, message, issuer, revoked, dateIssued, lengthInSeconds);
    }
}
