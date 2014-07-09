package net.cogzmc.punishments.types.impl.model;

import lombok.EqualsAndHashCode;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.punishments.types.HumanFriendlyName;
import org.bson.types.ObjectId;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@HumanFriendlyName("TempBan")
public final class TemporaryBan extends MongoTemporaryPunishment {
    public TemporaryBan(ObjectId id, COfflinePlayer target, String message, COfflinePlayer issuer, boolean revoked, Date dateIssued, Integer lengthInSeconds) {
        super(id, target, message, issuer, revoked, dateIssued, lengthInSeconds);
    }
}
