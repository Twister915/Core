package net.cogzmc.punishments.types.impl.model;

import lombok.EqualsAndHashCode;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.punishments.types.impl.TargetOnlinesOnly;
import org.bson.types.ObjectId;

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
