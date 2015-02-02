package net.cogzmc.gameapi.model.game;

public interface GameMeta {
    String getName();
    String getShortName();
    String getVersion();
    String getDescription();
    ChatColor getPrimaryColor();
    ChatColor getSecondaryColor();
    ImmutableList<String> getAuthors();
}
