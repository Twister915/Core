package net.cogzmc.core.gui;

import net.cogzmc.core.player.CPlayer;

/**
 *
 */
public interface GraphicalInterface {
    /**
     *
     * @param player
     */
    void open(CPlayer player);

    /**
     *
     * @param player
     */
    void close(CPlayer player);

    /**
     *
     * @param players
     */
    void open(Iterable<CPlayer> players);

    /**
     *
     * @param players
     */
    void close(Iterable<CPlayer> players);

    /**
     *
     * @return
     */
    ImmutableList<CPlayer> getCurrentObservers();
}
