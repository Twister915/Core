package net.cogzmc.gameapi.model.game;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.arena.Point;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * The action delegate will be told by a game <b>after</b> something happens so that the game logic can be executed
 * based on the action that has occurred.
 *
 * @param <ArenaType> The type of {@link net.cogzmc.gameapi.model.arena.Arena} that the game
 */
public abstract class GameActionDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> {
    protected GameActionDelegate(Game<ArenaType> game) {
        super(game);
    }

    protected void onGameStart() {}
    protected void onPlayerLeaveGame(CPlayer player) {}
    protected void onSpectatorJoinGame(CPlayer player) {}
    protected void onPlayerBecomeSpectator(CPlayer player) {}
    protected void onPlayerMove(CPlayer player, Point from, Point to) {}
    protected void onPlayerInteract(CPlayer player, Point pointClicked, Action action) {}
    protected void onPlayerPickup(CPlayer cPlayer, Item item) {}
    protected void onPlayerDrop(CPlayer cPlayer, ItemStack item) {}
    protected void onRemoveArmor(CPlayer cPlayer, ItemStack item) {}
    protected void onAddArmor(CPlayer cPlayer, ItemStack item) {}
    protected void onPlayerEat(CPlayer cPlayer, ItemStack eating) {}
    protected void onPlayerChat(CPlayer cPlayer, String message) {}
    protected void onBlockPlace(CPlayer cPlayer, Block block, Point point) {}
    protected void onBlockBreak(CPlayer cPlayer, Block block, Point point) {}
    protected void onEnterVehicle(CPlayer cPlayer, Vehicle vehicle) {}
    protected void onRegainHealth(CPlayer cPlayer, Double healthRegaining) {}
    protected void onShootBow(CPlayer cPlayer) {}
    protected void onFillBucket(CPlayer cPlayer, Material bucket) {}
    protected void onEmptyBucket(CPlayer cPlayer, Material bucket) {}
    protected void onExpPickup(CPlayer cPlayer) {}
}
