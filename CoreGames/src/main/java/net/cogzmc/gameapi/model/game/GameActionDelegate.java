package net.cogzmc.gameapi.model.game;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.core.util.Point;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The action delegate will be told by a game <b>after</b> something happens so that the game logic can be executed
 * based on the action that has occurred.
 *
 * @param <ArenaType> The type of {@link net.cogzmc.gameapi.model.arena.Arena} that the game
 */
public abstract class GameActionDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> implements GameObserver {
    protected GameActionDelegate(Game<ArenaType> game) {
        super(game);
    }

    @Override
    public void onGameStart() {}

    @Override
    public void onGameEnd() {}

    @Override
    public void onPlayerJoinGame(CPlayer player) {}

    @Override
    public void onPlayerLeaveGame(CPlayer player) {}

    @Override
    public void onSpectatorJoinGame(CPlayer player) {}

    @Override
    public void onPlayerBecomeSpectator(CPlayer player) {}

    @Override
    public void onPlayerMove(CPlayer player, Point from, Point to) {}

    @Override
    public void onPlayerInteract(CPlayer player, Point pointClicked, Action action) {}

    @Override
    public void onPlayerPickup(CPlayer cPlayer, Item item) {}

    @Override
    public void onPlayerDrop(CPlayer cPlayer, ItemStack item) {}

    @Override
    public void onPlayerEat(CPlayer cPlayer, ItemStack eating) {}

    @Override
    public void onPlayerChat(CPlayer cPlayer, String message) {}

    @Override
    public void onBlockPlace(CPlayer cPlayer, Block block, Point point) {}

    @Override
    public void onBlockBreak(CPlayer cPlayer, Block block, Point point) {}

    @Override
    public void onEnterVehicle(CPlayer cPlayer, Vehicle vehicle) {}

    @Override
    public void onRegainHealth(CPlayer cPlayer, Double healthRegaining) {}

    @Override
    public void onShootBow(CPlayer cPlayer) {}

    @Override
    public void onFillBucket(CPlayer cPlayer, Material bucket) {}

    @Override
    public void onEmptyBucket(CPlayer cPlayer, Material bucket) {}

    @Override
    public void onExpPickup(CPlayer cPlayer) {}

    @Override
    public void onPlayerKilled(CPlayer player, CPlayer killer, EntityDamageEvent.DamageCause cause) {}

    @Override
    public void onPlayerKilled(CPlayer player, LivingEntity killer, EntityDamageEvent.DamageCause cause) {}

    @Override
    public void onPlayerDead(CPlayer player, EntityDamageEvent.DamageCause cause) {}

    @Override
    public void onPlayerDamage(CPlayer player, EntityDamageEvent.DamageCause cause, Integer damageTaken) {}

    @Override
    public void onPlayerDamage(CPlayer target, CPlayer attacker, Integer damageTaken, EntityDamageEvent.DamageCause cause) {}
}
