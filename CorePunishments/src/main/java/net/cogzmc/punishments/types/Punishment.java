package net.cogzmc.punishments.types;

import net.cogzmc.core.player.COfflinePlayer;

import java.util.Date;

public interface Punishment {
    String getMessage();
    COfflinePlayer getTarget();
    COfflinePlayer getIssuer();
    Date getDateIssued();
    boolean isActive();
    void setActive(boolean active);
}
