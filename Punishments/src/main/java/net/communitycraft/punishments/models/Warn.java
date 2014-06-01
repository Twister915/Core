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
public final class Warn extends AbstractPunishment {
    private String warnMessage;
    private COfflinePlayer target;
    private COfflinePlayer issuer;
    private Date dateIssued;

	@Override
	public String getReason() {
		return warnMessage;
	}

	@Override
	public Date getDate() {
		return dateIssued;
	}
}
