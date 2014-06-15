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
import org.bukkit.inventory.ItemStack;

@Data
public final class GameListener<ArenaType extends Arena> implements Listener {
    private final Game<ArenaType> game;

    private boolean validateEvent(Event event) {
        if (event instanceof PlayerEvent && (!game.isPlaying(resolvePlayer(((PlayerEvent) event).getPlayer())))) return false;
        if (event instanceof InventoryInteractEvent && !game.isPlaying(resolvePlayer((Player) ((InventoryInteractEvent) event).getWhoClicked()))) return false;

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
        //Steps
        //- Base interact
        //- Potions
        //- Eating
        // - Bow
        CPlayer cPlayer = resolvePlayer(event.getPlayer());
        Location clickedLocation;
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
            case LEFT_CLICK_BLOCK:
            case PHYSICAL:
                clickedLocation = event.getClickedBlock().getLocation();
                break;
            default:
                clickedLocation = event.getPlayer().getLocation();
                break;
        }
        if (!game.getRuleDelegate().canPlayerInteract(cPlayer, fromLocation(clickedLocation), event.getAction())) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemInHand = event.getPlayer().getItemInHand();
        if (event.getAction() != Action.PHYSICAL && itemInHand != null) {
            switch (itemInHand.getType()) {
                case POTION:
                    if (game.getRuleDelegate().canDrinkPotion(cPlayer)) {
                        event.setCancelled(true);
                        return;
                    }
                case
            }
        }
    }
}
