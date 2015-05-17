package net.cogzmc.core.gui;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.cogzmc.core.Core;
import net.cogzmc.core.effect.npc.ClickAction;
import net.cogzmc.core.modular.command.EmptyHandlerException;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 *
 */
@Data
public class InventoryGraphicalInterface implements GraphicalInterface, Listener {
    protected final List<CPlayer> observers = new LinkedList<>();
    protected final String title;
    protected Inventory inventory;
    @Getter(AccessLevel.NONE) protected final Map<Integer, InventoryButton> inventoryButtons = new HashMap<>();
    @Setter(AccessLevel.NONE) protected Set<Integer> updatedSlots = new HashSet<>();

    public InventoryGraphicalInterface(Integer size, String title) {
        if (size % 9 != 0) throw new IllegalArgumentException("The size of an inventory must be divisible by 9 evenly.");
        this.title = title;
        inventory = Bukkit.createInventory(null, size, title);
        Core.getInstance().registerListener(this);
    }

    @Override
    public final void open(CPlayer player) {
        if (observers.contains(player)) return;
        observers.add(player);
        player.getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public final void close(CPlayer player) {
        if (!observers.contains(player)) return;
        observers.remove(player);
        player.getBukkitPlayer().closeInventory();
        onClose(player);
    }

    @Override
    public final void open(Iterable<CPlayer> players) {
        for (CPlayer player : players) open(player);
    }

    @Override
    public final void close(Iterable<CPlayer> players) {
        for (CPlayer player : players) close(player);
    }

    @Override
    public final ImmutableList<CPlayer> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    /**
     * Adds a button, will replace default to the next available slot. Will throw an {@link java.lang.IllegalStateException} if there is no room remaining.
     * @param button The {@link net.cogzmc.core.gui.InventoryButton} to add to the inventory GUI.
     */
    public final void addButton(InventoryButton button) {
        Integer nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == null) throw new IllegalStateException("Unable to place the button in the inventory, no room remains!");
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds a button to the GUI at a specific location and will overwrite the current button at that location.
     * @param button The {@link net.cogzmc.core.gui.InventoryButton} to add to the inventory GUI.
     * @param slot The slot to place that button at.
     */
    public final void addButton(InventoryButton button, Integer slot) {
        inventoryButtons.put(slot, button);
        markForUpdate(slot);
    }

    /**
     *
     * @param button
     * @param slot
     */
    public final void moveButton(InventoryButton button, Integer slot) {
        removeButton(button);
        addButton(button, slot);
    }

    /**
     *
     * @param button
     */
    public final void markForUpdate(InventoryButton button) {
        markForUpdate(getSlotFor(button));
    }

    /**
     *
     * @param slot
     */
    public final void markForUpdate(Integer slot) {
        updatedSlots.add(slot);
    }

    /**
     *
     * @param button
     * @return
     */
    public final Integer getSlotFor(InventoryButton button) {
        for (Map.Entry<Integer, InventoryButton> integerInventoryButtonEntry : inventoryButtons.entrySet()) {
            if (integerInventoryButtonEntry.getValue().equals(button)) return integerInventoryButtonEntry.getKey();
        }
        return -1;
    }

    /**
     *
     * @param button
     */
    public final void removeButton(InventoryButton button) {
        clearSlot(getSlotFor(button));
    }

    /**
     *
     * @param slot
     */
    public void clearSlot(Integer slot) {
        if (!inventoryButtons.containsKey(slot)) return;
        inventoryButtons.remove(slot);
        markForUpdate(slot);
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void onClose(CPlayer onlinePlayer) {}

    public final boolean isFilled(Integer slot) {return inventoryButtons.containsKey(slot);}
    /**
     *
     */
    public final void updateInventory() {
        for (int x = 0; x < inventory.getSize(); x++) {
            InventoryButton inventoryButton = inventoryButtons.get(x);
            if (inventoryButton == null && inventory.getItem(x) != null) {
                inventory.setItem(x, null);
                continue;
            }
            if ((inventory.getItem(x) == null && inventoryButton != null) || updatedSlots.contains(x)) {
                assert inventoryButton != null;
                inventory.setItem(x, inventoryButton.getStack());
            }
        }
        for (CPlayer observer : observers) {
            //noinspection deprecation
            observer.getBukkitPlayer().updateInventory();
        }
        updatedSlots = new HashSet<>();
    }

    private Integer getNextOpenSlot() {
        Integer nextSlot = 0;
        for (Integer integer : inventoryButtons.keySet()) {
            if (integer.equals(nextSlot)) nextSlot = integer+1;
        }
        return nextSlot >= inventory.getSize() ? null : nextSlot;
    }

    /* Event Handlers */
    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlayerLeave(PlayerQuitEvent event) {
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        if (observers.contains(onlinePlayer)) {
            this.observers.remove(onlinePlayer);
            Core.logDebug("Removed observer of inventory GUI " + title + " during disconnect. Most likely a timeout!");
        }
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        Player player = (Player) event.getPlayer();
        CPlayer onlinePlayer = Core.getOnlinePlayer(player);
        this.observers.remove(onlinePlayer);
        onClose(onlinePlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().equals(inventory))) return;
        CPlayer player = Core.getOnlinePlayer((Player) event.getWhoClicked());
        InventoryButton inventoryButton = inventoryButtons.get(event.getSlot());
        if (player == null)
            throw new IllegalStateException("Somehow, someone who was null clicked on a slot that was null or had no button...");
        if (inventoryButton == null) return;
        try {
            inventoryButton.onPlayerClick(player, getActionTypeFor(event.getClick()));
        } catch (EmptyHandlerException e) {
            player.playSoundForPlayer(Sound.NOTE_PLING);
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerInventoryMove(InventoryMoveItemEvent event) {
        if (!event.getDestination().equals(inventory)) return;
        event.setCancelled(true);
    }

    public final ImmutableList<InventoryButton> getButtons() {
        return ImmutableList.copyOf(inventoryButtons.values());
    }

    private static ClickAction getActionTypeFor(ClickType click) {
        switch (click) {
            case RIGHT:
            case SHIFT_RIGHT:
                return ClickAction.RIGHT_CLICK;
            default:
                return ClickAction.LEFT_CLICK;
        }
    }
}
