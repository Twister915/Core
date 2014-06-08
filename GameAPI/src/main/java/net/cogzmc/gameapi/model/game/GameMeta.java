package net.cogzmc.gameapi.model.game;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

public interface GameMeta {
    String getName();
    String getShortName();
    String getVersion();
    String getDescription();
    ChatColor getPrimaryColor();
    ChatColor getSecondaryColor();
    ImmutableList<String> getAuthors();

}
