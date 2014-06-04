package net.cogzmc.core.gui;

import com.google.common.collect.ImmutableList;
import net.cogzmc.core.player.CPlayer;

public interface GraphicalInterface {
    void open(CPlayer player);
    void close(CPlayer player);
    void open(Iterable<CPlayer> players);
    void close(Iterable<CPlayer> players);

    ImmutableList<CPlayer> getCurrentObservers();
}
