package net.cogzmc.punishments;

import net.cogzmc.punishments.PunishmentManager;
import net.cogzmc.punishments.types.PunishmentException;
import net.cogzmc.punishments.types.TimedPunishment;

public interface TimedPunishmentManager<T extends TimedPunishment> extends PunishmentManager<T> {
    T punish(COfflinePlayer target, String reason, COfflinePlayer issuer, Integer lengthInSeconds) throws PunishmentException;
}
