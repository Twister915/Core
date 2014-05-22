package net.communitycraft.punishments.models;

import net.communitycraft.core.player.COfflinePlayer;

public interface PunishmentModel {
    COfflinePlayer getTarget();
    COfflinePlayer getIssuer();
    String getReason();
}
