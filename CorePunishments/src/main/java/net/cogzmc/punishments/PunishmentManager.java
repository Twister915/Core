package net.cogzmc.punishments;

import net.cogzmc.punishments.types.Punishment;
import net.cogzmc.punishments.types.PunishmentException;

import java.util.List;

public interface PunishmentManager<T extends Punishment> extends CPlayerConnectionListener {
    T punish(COfflinePlayer target, String reason, COfflinePlayer issuer) throws PunishmentException;
    List<T> getPunishmentsFor(COfflinePlayer target);
    T getActivePunishmentFor(COfflinePlayer target);
    void revokePunishment(T punishment);
}
