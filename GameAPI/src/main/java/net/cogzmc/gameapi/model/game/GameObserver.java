package net.cogzmc.gameapi.model.game;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Point;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface GameObserver {
    void onGameStart();
    void onPlayerJoinGame(CPlayer player);
    void onGameEnd();
    void onPlayerLeaveGame(CPlayer player);
    void onSpectatorJoinGame(CPlayer player);
    void onPlayerBecomeSpectator(CPlayer player);
    void onPlayerMove(CPlayer player, Point from, Point to);
    void onPlayerInteract(CPlayer player, Point pointClicked, Action action);
    void onPlayerPickup(CPlayer cPlayer, Item item);
    void onPlayerDrop(CPlayer cPlayer, ItemStack item);
    void onPlayerEat(CPlayer cPlayer, ItemStack eating);
    void onPlayerChat(CPlayer cPlayer, String message);
    void onBlockPlace(CPlayer cPlayer, Block block, Point point);
    void onBlockBreak(CPlayer cPlayer, Block block, Point point);
    void onEnterVehicle(CPlayer cPlayer, Vehicle vehicle);
    void onRegainHealth(CPlayer cPlayer, Double healthRegaining);
    void onShootBow(CPlayer cPlayer);
    void onFillBucket(CPlayer cPlayer, Material bucket);
    void onEmptyBucket(CPlayer cPlayer, Material bucket);
    void onExpPickup(CPlayer cPlayer);
    void onPlayerKilled(CPlayer player, CPlayer killer, DamageCause cause);
    void onPlayerKilled(CPlayer player, LivingEntity killer, DamageCause cause);
    void onPlayerDead(CPlayer player, DamageCause cause);
    void onPlayerDamage(CPlayer target, CPlayer attacker, Integer damageTaken, DamageCause cause);
    void onPlayerDamage(CPlayer player, DamageCause cause, Integer damageTaken);
}
