package net.cogzmc.gameapi.model.game;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.arena.Point;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

@Data
public final class GameListener implements Listener {
    private final Game<?> game;

    @SuppressWarnings("RedundantIfStatement")
    private boolean validateEvent(Event event) {
        if (event instanceof PlayerEvent && (!game.isInvolvedInGame(resolvePlayer(((PlayerEvent) event).getPlayer())))) return false;
        if (event instanceof InventoryInteractEvent && !game.isInvolvedInGame(resolvePlayer((Player) ((InventoryInteractEvent) event).getWhoClicked()))) return false;
        //If it's an entityEvent, they're not a player, or the player who they are is not involved in the game.
        if (event instanceof EntityEvent && (!(((EntityEvent) event).getEntity() instanceof Player) || (!game.isInvolvedInGame(resolvePlayer((Player) ((EntityEvent) event).getEntity()))))) return false;
        return true;
    }

    private CPlayer resolvePlayer(Player player) {
        return Core.getOnlinePlayer(player);
    }

    private Point fromLocation(Location location) {
        return Point.of(location);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!validateEvent(event)) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        Point fromPoint = fromLocation(from);
        Point toPoint = fromLocation(to);
        if (!game.getRuleDelegate().canPlayerMove(cPlayer, fromPoint, toPoint)) {
            event.setTo(new Location(from.getWorld(), from.getX(), from.getY(), from.getZ(), to.getPitch(), to.getYaw()));
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onPlayerMove(cPlayer, fromPoint, toPoint);} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer)) { //Spectator
            event.setCancelled(true);
            return;
        }
        Location clickedLocation;
        Action action = event.getAction();
        switch (action) {
            case RIGHT_CLICK_BLOCK:
            case LEFT_CLICK_BLOCK:
            case PHYSICAL:
                clickedLocation = event.getClickedBlock().getLocation();
                break;
            default:
                clickedLocation = event.getPlayer().getLocation();
                break;
        }
        Point point = fromLocation(clickedLocation);
        if (!game.getRuleDelegate().canPlayerInteract(cPlayer, point, action)) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemInHand = event.getPlayer().getItemInHand();
        if (action != Action.PHYSICAL && itemInHand != null) {
            switch (itemInHand.getType()) {
                case POTION:
                    if (!game.getRuleDelegate().canDrinkPotion(cPlayer)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case BOW:
                    if (game.getRuleDelegate().canShootBow(cPlayer)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                case COOKED_BEEF:
                case RAW_BEEF:
                case APPLE:
                case GOLDEN_APPLE:
                case COOKED_CHICKEN:
                case RAW_CHICKEN:
                case BAKED_POTATO:
                case POTATO_ITEM:
                case POISONOUS_POTATO:
                case BREAD:
                case CARROT_ITEM:
                case CAKE:
                case COOKED_FISH:
                case COOKIE:
                case PUMPKIN_PIE:
                case ROTTEN_FLESH:
                case SPIDER_EYE:
                case MUSHROOM_SOUP:
                case MELON:
                    if (game.getRuleDelegate().canEat(cPlayer, itemInHand)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
            }
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onPlayerInteract(cPlayer, point, action);} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canPickup(cPlayer, event.getItem())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onPlayerPickup(cPlayer, event.getItem());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canDrop(cPlayer, event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onPlayerDrop(cPlayer, event.getItemDrop().getItemStack());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canEat(cPlayer, event.getItem())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onPlayerEat(cPlayer, event.getItem());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        // Spectator stuff will be done later on
        if (!game.getRuleDelegate().canChat(cPlayer, event.getMessage())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onPlayerChat(cPlayer, event.getMessage());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        Block block = event.getBlockPlaced();
        Point of = Point.of(block);
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canPlaceBlock(cPlayer, block, of)) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onBlockPlace(cPlayer, block, of);} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        Block block = event.getBlock();
        Point of = Point.of(block);
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canBreakBlock(cPlayer, block, of)) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onBlockBreak(cPlayer, block, of);} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEnterVehicle(VehicleEnterEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer((Player) event.getEntered());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canEnterVehicle(cPlayer, event.getVehicle())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onEnterVehicle(cPlayer, event.getVehicle());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer((Player) event.getEntity());
        if (!game.getRuleDelegate().canRegainHealth(cPlayer, event.getAmount())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onRegainHealth(cPlayer, event.getAmount());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerShootBowEvent(EntityShootBowEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer((Player) event.getEntity());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canShootBow(cPlayer)) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onShootBow(cPlayer);} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFillBucketEvent(PlayerBucketFillEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canFillBucket(cPlayer, event.getBucket())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onFillBucket(cPlayer, event.getBucket());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEmptyBucketEvent(PlayerBucketEmptyEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canEmptyBucket(cPlayer, event.getBucket())) {
            event.setCancelled(true);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onEmptyBucket(cPlayer, event.getBucket());} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!validateEvent(event)) return;
        if (!(event.getEntity() instanceof ExperienceOrb)) return;
        CPlayer cPlayer = resolvePlayer((Player) event.getEntity());
        if (game.isSpectating(cPlayer) || !game.getRuleDelegate().canPickupEXP(cPlayer)) {
            event.setCancelled(true);
            event.setTarget(null);
            return;
        }
        for (GameObserver gameObserver : game.getObservers()) {
            try {gameObserver.onExpPickup(cPlayer);} catch (Exception e) {e.printStackTrace();}
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        //TODO canTakeFromInventory, canRemoveArmor, canAddArmor
    }
}