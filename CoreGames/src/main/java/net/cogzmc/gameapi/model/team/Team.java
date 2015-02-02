package net.cogzmc.gameapi.model.team;

public interface Team {
    String getName();
    boolean canJoin(CPlayer player);
}
