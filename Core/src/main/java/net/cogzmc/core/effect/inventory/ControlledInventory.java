package net.cogzmc.core.effect.inventory;

import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import net.cogzmc.core.player.CPlayerConnectionListener;
import net.cogzmc.core.player.CPlayerJoinException;
import net.cogzmc.core.player.CooldownUnexpiredException;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
public abstract class ControlledInventory implements Listener, CPlayerConnectionListener {
    private final Map<Integer, ControlledInventoryButton> buttons = new HashMap<>();
    private final Set<CPlayer> players = new HashSet<>();

    public ControlledInventory() {
        reload();
    }

    protected abstract ControlledInventoryButton getNewButtonAt(Integer slot);

    @Override
    public final void onPlayerLogin(CPlayer player, InetAddress address) throws CPlayerJoinException {}

    @Override
    public final void onPlayerDisconnect(CPlayer player) {
        players.remove(player);
    }

    public final void reload() {
        for (int i = 0; i < 36; i++) {
            ControlledInventoryButton newButtonAt = getNewButtonAt(i);
            if (newButtonAt == null) continue;
            buttons.put(i, newButtonAt);
        }
        for (CPlayer player : players) {
            updateForPlayer(player);
        }
    }

    public final void updateItems() {
        for (CPlayer player : players) {
            updateForPlayer(player);
        }
    }

    public final void setActive(CPlayer player) {
        players.add(player);
        updateForPlayer(player);
    }

    public final void remove(CPlayer player) {
        if (!players.contains(player)) return;
        clearForPlayer(player);
        players.remove(player);
    }

    protected void updateForPlayer(CPlayer player) {
        Player bukkitPlayer = player.getBukkitPlayer();
        for (Map.Entry<Integer, ControlledInventoryButton> entry : buttons.entrySet()) {
            bukkitPlayer.getInventory().setItem(entry.getKey(), entry.getValue().getStack(player));
        }
        bukkitPlayer.updateInventory();
    }

    private void clearForPlayer(CPlayer player) {
        Player bukkitPlayer = player.getBukkitPlayer();
        for (Integer integer : buttons.keySet()) {
            bukkitPlayer.getInventory().setItem(integer, null);
        }
        bukkitPlayer.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public final void onPlayerInventoryMove(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        CPlayer onlinePlayer = Core.getOnlinePlayer((Player) event.getWhoClicked());
        if (!players.contains(onlinePlayer)) return;
        if (buttons.keySet().contains(event.getSlot())
                && players.contains(onlinePlayer)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getAction() == Action.PHYSICAL) return;
        ControlledInventoryButton controlledInventoryButton = buttons.get(event.getPlayer().getInventory().getHeldItemSlot());
        if (controlledInventoryButton == null) return;
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        if (!players.contains(onlinePlayer)) return;
            try {
            onlinePlayer.getCooldownManager().testCooldown((controlledInventoryButton.hashCode() + "_inv"), 1L, TimeUnit.SECONDS);
        } catch (CooldownUnexpiredException e) {
            if (e.getTimeRemaining() > 800)
                return;
            else //TODO make a sound and send a message.
                return;
        }
        controlledInventoryButton.onUse(onlinePlayer);
        updateForPlayer(onlinePlayer);
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        if (!players.contains(onlinePlayer)) return;
        if (buttons.get(event.getPlayer().getInventory().getHeldItemSlot()) != null) {
            event.setCancelled(false);
            event.getItemDrop().remove();
            updateForPlayer(onlinePlayer);
            try {
                onlinePlayer.getCooldownManager().testCooldown(hashCode() + "_inv_drop", 500L, TimeUnit.MILLISECONDS, false);
            } catch (CooldownUnexpiredException e) {
                onlinePlayer.kickPlayer(ChatColor.RED + "Spamming inventory drops (more than 1 per second)");
            }
        }
    }


    protected final Set<Map.Entry<Integer, ControlledInventoryButton>> getButtons() {
        return buttons.entrySet();
    }
}
