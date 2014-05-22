package net.communitycraft.punishments.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.communitycraft.core.model.Model;
import net.communitycraft.core.model.ModelField;
import net.communitycraft.core.player.COfflinePlayer;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ModelField
@AllArgsConstructor
public final class Warn extends Model {
    private String warnMessage;
    private COfflinePlayer target;
    private COfflinePlayer issuer;
    private Date dateIssued;
}
