package net.cogzmc.punishments.types.impl.model;

import net.cogzmc.punishments.types.Punishment;

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
