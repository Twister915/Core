package net.cogzmc.punishments.types.impl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.cogzmc.core.player.COfflinePlayer;
import net.cogzmc.punishments.types.Punishment;
import org.bson.types.ObjectId;

import java.util.Date;

@EqualsAndHashCode
@AllArgsConstructor
@Data
public abstract class MongoPunishment implements Punishment {
    private ObjectId mongoId;
    private final COfflinePlayer target;
    private final String message;
    private final COfflinePlayer issuer;
    @NonNull private boolean active;
    private final Date dateIssued;
}
