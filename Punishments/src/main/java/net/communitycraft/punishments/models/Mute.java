package net.communitycraft.punishments.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.core.model.ModelField;
import net.cogzmc.core.player.COfflinePlayer;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ModelField
@AllArgsConstructor
public final class Mute extends AbstractPunishment implements ExpirablePunishment {
    private String reason;
    private COfflinePlayer issuer;
    private COfflinePlayer target;
    private Date dateMuted;
    private Long length; //In seconds

	@Override
	public Date getDate() {
		return dateMuted;
	}

	public boolean isExpired() {
		return new Date().after(getExpirationDate());
	}

	public Date getExpirationDate() {
		return new Date(dateMuted.getTime() + length);
	}
}
