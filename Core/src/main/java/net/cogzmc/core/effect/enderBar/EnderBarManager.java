package net.cogzmc.core.effect.enderBar;

import net.cogzmc.core.Core;
import net.cogzmc.core.effect.npc.mobs.MobNPCEnderDragon;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.core.util.Point;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class EnderBarManager implements CPlayerConnectionListener {
    final Map<CPlayer, MobNPCEnderDragon> enderBars = new HashMap<>();
    private int lastId = 3000;

    /**
     * Creates a manager for the EnderBar system.
     */
    public EnderBarManager() {
        Core.getPlayerManager().registerCPlayerConnectionListener(this); //Registers us as a listener so we can remove our CPlayer references when they disconnect.
        Core.getInstance().registerListener(new EnderBarListener(this)); //Also need to move the ender dragon around.
    }

    /**
     * Creates an Ender Dragon for the player if it does not exist, then updates the text for the dragon.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to set ender bar text for.
     * @param text The text to set the bar to.
     */
    public void setTextFor(CPlayer player, String text) {
        createIfDoesNotExist(player);
        MobNPCEnderDragon mobNPCEnderDragon = enderBars.get(player);
        mobNPCEnderDragon.setCustomName(text);
        mobNPCEnderDragon.update();
    }

    /**
     * Creates an Ender Dragon for the player if it does not exist, then updates the health percentage for that dragon.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to set ender bar health for.
     * @param health The health to set the ender dragon's bar to.
     */
    public void setHealthPercentageFor(CPlayer player, Float health) {
        createIfDoesNotExist(player);
        MobNPCEnderDragon mobNPCEnderDragon = enderBars.get(player);
        mobNPCEnderDragon.setHealth(health * 200F);
        mobNPCEnderDragon.update();
    }

    /**
     * Hides the bar for the specified player if it is currently visible.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to hide the bar for.
     */
    public void hideBarFor(CPlayer player) {
        if (!enderBars.containsKey(player)) return;
        MobNPCEnderDragon enderBar = enderBars.get(player);
        if (enderBar.isSpawned()) enderBar.despawn();
    }

    /**
     * Creates an ender dragon if the player does not already have one, and then shows it if it is hidden.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to show the ender bar for.
     */
    public void showBarFor(CPlayer player) {
        MobNPCEnderDragon enderBar = enderBars.get(player);
        if (!createIfDoesNotExist(player) && !enderBar.isSpawned()) enderBar.spawn();
    }

    private boolean createIfDoesNotExist(CPlayer player) {
        if (enderBars.containsKey(player)) return false;
        Player bukkitPlayer = player.getBukkitPlayer();
        HashSet<CPlayer> cPlayers = new HashSet<>();
        cPlayers.add(player);
        Point of = Point.of(bukkitPlayer.getLocation());
        of.setY(-300D);
        MobNPCEnderDragon enderDragon = new MobNPCEnderDragon(of, bukkitPlayer.getWorld(), cPlayers, "Ender Dragon");
        enderBars.put(player, enderDragon);
        enderDragon.spawn();
        return true;
    }

    Integer getNextId() {
        lastId++;
        return lastId;
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        this.enderBars.remove(player);
    }
}
