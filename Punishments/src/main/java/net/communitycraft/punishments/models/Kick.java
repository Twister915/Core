package net.communitycraft.punishments.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.core.model.Model;
import net.cogzmc.core.model.ModelField;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ModelField
@Data
@AllArgsConstructor
public final class Kick extends Model implements PunishmentModel {
    private String reason;
    private COfflinePlayer target;
    private COfflinePlayer issuer;
    private Date dateIssued;
}
