package net.cogzmc.gameapi.model.team;

import net.cogzmc.core.player.CPlayer;

public interface Team {
    String getName();
    boolean canJoin(CPlayer player);
}
