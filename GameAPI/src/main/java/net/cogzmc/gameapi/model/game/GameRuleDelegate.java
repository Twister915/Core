package net.cogzmc.gameapi.model.game;

import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.arena.Point;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class GameRuleDelegate<ArenaType extends Arena> extends GameDelegate<ArenaType> {
    protected GameRuleDelegate(Game<ArenaType> game) {
        super(game);
    }

    protected boolean canPlayerMove(CPlayer player, Point from, Point to) {return true;}
    protected boolean canPlayerInteract(CPlayer player, Point pointClicked, Action action) {return  true;}
    protected boolean canPickup(CPlayer player, Item item) {return true;}
    protected boolean canDrop(CPlayer player, ItemStack drop) {return true;}
    protected boolean canTakeFromInventory(CPlayer player, Inventory inventory, ItemStack stack) {return true;}
    protected boolean canAttackEntity(CPlayer player, LivingEntity entity) {return true;}
    protected boolean canAttackPlayer(CPlayer player, CPlayer target) {return true;}
    protected boolean canTakeDamage(CPlayer player, EntityDamageEvent.DamageCause cause) {return true;}
    protected boolean canRemoveArmor(CPlayer player, ItemStack armorPart) {return true;}
    protected boolean canAddArmor(CPlayer player, ItemStack armorPart) {return true;}
    protected boolean canEat(CPlayer player, ItemStack eating) {return true;}
    protected boolean canChat(CPlayer player, String message) {return true;}
    protected boolean canPlaceBlock(CPlayer player, Block placed, Point placedAt) {return true;}
    protected boolean canBreakBlock(CPlayer player, Block removed, Point removedFrom) {return true;}
    protected boolean canEnterVehicle(CPlayer player, Vehicle vehicle) {return true;}
    protected boolean canRegainHealth(CPlayer player, Double healthRegaining) {return true;}
    protected boolean canShootBow(CPlayer player) {return true;}
    protected boolean canDrinkPotion(CPlayer player) {return true;}
    protected boolean canPickupEXP(CPlayer player) {return true;}
    protected boolean canFillBucket(CPlayer player, ItemStack bucket) {return true;}
}
