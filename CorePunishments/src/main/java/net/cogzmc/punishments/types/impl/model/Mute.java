package net.cogzmc.punishments.types.impl.model;

import lombok.EqualsAndHashCode;
import net.cogzmc.core.player.COfflinePlayer;
import org.bson.types.ObjectId;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public final class Mute extends MongoPunishment {
    public Mute(ObjectId objectId, COfflinePlayer target, String message, COfflinePlayer issuer, boolean revoked, Date dateIssued) {
        super(objectId, target, message, issuer, revoked, dateIssued);
    }
}
