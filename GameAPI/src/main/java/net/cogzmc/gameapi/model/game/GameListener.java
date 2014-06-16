package net.cogzmc.gameapi.model.game;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.gameapi.model.arena.Arena;
import net.cogzmc.gameapi.model.arena.Point;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import sun.net.www.content.text.plain;

@Data
public final class GameListener<ArenaType extends Arena> implements Listener {
    private final Game<ArenaType> game;

    private boolean validateEvent(Event event) {
        if (event instanceof PlayerEvent && (!game.isInvolvedInGame(resolvePlayer(((PlayerEvent) event).getPlayer())))) return false;
        if (event instanceof InventoryInteractEvent && !game.isInvolvedInGame(resolvePlayer((Player) ((InventoryInteractEvent) event).getWhoClicked()))) return false;
        return true;
    }

    private CPlayer resolvePlayer(Player player) {
        return Core.getOnlinePlayer(player);
    }

    private Point fromLocation(Location location) {
        return Point.of(location);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        game.getActionDelegate().onPlayerMove(cPlayer, fromPoint, toPoint);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        game.getActionDelegate().onPlayerInteract(cPlayer, point, action);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        if (!validateEvent(event)) return;
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        if (game.isSpectating(cPlayer)) {
            event.setCancelled(true);
            return;
        }
        if (!game.getRuleDelegate().canPickup(cPlayer, event.getItem())){
            event.setCancelled(true);
            return;
        }
        game.getActionDelegate().onPlayerPickup(cPlayer, event.getItem());
    }
}
