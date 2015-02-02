package net.cogzmc.core.effect.enderBar;

import net.cogzmc.core.Core;
import net.cogzmc.core.effect.npc.mobs.MobNPCWither;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.core.util.Point;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class EnderBarManager implements CPlayerConnectionListener {
    final Map<CPlayer, MobNPCWither> witherBar = new HashMap<>();

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
        MobNPCWither mobNPCWither = witherBar.get(player);
        mobNPCWither.setCustomName(text);
        mobNPCWither.update();
    }

    /**
     * Creates an Ender Dragon for the player if it does not exist, then updates the health percentage for that dragon.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to set ender bar health for.
     * @param health The health to set the ender dragon's bar to.
     */
    public void setHealthPercentageFor(CPlayer player, Float health) {
        createIfDoesNotExist(player);
        MobNPCWither mobNPCWither = witherBar.get(player);
        mobNPCWither.setHealth(health * mobNPCWither.getMaximumHealth());
        mobNPCWither.update();
    }

    /**
     * Hides the bar for the specified player if it is currently visible.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to hide the bar for.
     */
    public void hideBarFor(CPlayer player) {
        if (!witherBar.containsKey(player)) return;
        MobNPCWither witherBar = this.witherBar.get(player);
        if (witherBar.isSpawned()) witherBar.despawn();
    }

    /**
     * Creates an ender dragon if the player does not already have one, and then shows it if it is hidden.
     * @param player The {@link net.cogzmc.core.player.CPlayer} to show the ender bar for.
     */
    public void showBarFor(CPlayer player) {
        MobNPCWither enderBar = witherBar.get(player);
        if (!createIfDoesNotExist(player) && !enderBar.isSpawned()) {
            enderBar.spawn();
        }
    }

    private boolean createIfDoesNotExist(CPlayer player) {
        if (witherBar.containsKey(player)) return false;
        Player bukkitPlayer = player.getBukkitPlayer();
        HashSet<CPlayer> cPlayers = new HashSet<>();
        cPlayers.add(player);
        Point of = Point.of(getLocationFor(player));
        MobNPCWither wither = new MobNPCWither(of, bukkitPlayer.getWorld(), cPlayers, "Boss Bar");
        wither.setInvisible(true);
        wither.setInculnerableTime(881);
        witherBar.put(player, wither);
        wither.spawn();
        return true;
    }

    public static Location getLocationFor(CPlayer player) {
        Player bukkitPlayer = player.getBukkitPlayer();
        return bukkitPlayer.getEyeLocation().add(bukkitPlayer.getEyeLocation().getDirection().normalize().multiply(23));
    }

    @Override
    public void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {
    }

    @Override
    public void onPlayerDisconnect(CPlayer player) {
        if (!this.witherBar.containsKey(player)) return;
        MobNPCWither mobNPCWither = this.witherBar.get(player);
        if (mobNPCWither.isSpawned()) mobNPCWither.despawn();
        this.witherBar.remove(player);
    }
}
