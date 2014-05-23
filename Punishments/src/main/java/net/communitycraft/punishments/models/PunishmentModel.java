package net.communitycraft.punishments.models;

import net.cogzmc.core.player.COfflinePlayer;

public interface PunishmentModel {
    COfflinePlayer getTarget();
    COfflinePlayer getIssuer();
    String getReason();
}
