package net.cogzmc.punishments.types.impl.model;

import net.cogzmc.punishments.types.TimedPunishment;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class MongoTemporaryPunishment extends MongoPunishment implements TimedPunishment {
    public final Integer lengthInSeconds;

    public MongoTemporaryPunishment(ObjectId objectId, COfflinePlayer target, String message, COfflinePlayer issuer, boolean revoked, Date dateIssued, Integer lengthInSeconds) {
        super(objectId, target, message, issuer, revoked, dateIssued);
        this.lengthInSeconds = lengthInSeconds;
    }
}
