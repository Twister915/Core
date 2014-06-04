package net.cogzmc.core.gui;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import net.cogzmc.core.Core;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 *
 */
@Data
public final class InventoryGraphicalInterface implements GraphicalInterface, Listener {
    private final static Integer INVENTORY_SIZE = 36;

    private final List<CPlayer> observers = new LinkedList<>();
    private final String title;
    private Inventory inventory;
    private final Map<Integer, InventoryButton> inventoryButtons = new HashMap<>();
    private Set<Integer> updatedSlots = new HashSet<>();

    public InventoryGraphicalInterface(String title) {
        this.title = title;
        this.inventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);
        Core.getInstance().registerListener(this);
    }

    @Override
    public void open(CPlayer player) {
        if (observers.contains(player)) throw new IllegalStateException("This player already has the GUI open!");
        observers.add(player);
        player.getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public void close(CPlayer player) {
        if (!observers.contains(player)) throw new IllegalStateException("This player does not currently have the GUI open!");
        observers.remove(player);
        player.getBukkitPlayer().closeInventory();
    }

    @Override
    public void open(Iterable<CPlayer> players) {
        for (CPlayer player : players) open(player);
    }

    @Override
    public void close(Iterable<CPlayer> players) {
        for (CPlayer player : players) close(player);
    }

    @Override
    public ImmutableList<CPlayer> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    /**
     * Adds a button, will replace default to the next available slot. Will throw an {@link java.lang.IllegalStateException} if there is no room remaining.
     * @param button The {@link net.cogzmc.core.gui.InventoryButton} to add to the inventory GUI.
     */
    public void addButton(InventoryButton button) {
        Integer nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == null) throw new IllegalStateException("Unable to place the button in the inventory, no room remains!");
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds a button to the GUI at a specific location and will overwrite the current button at that location.
     * @param button The {@link net.cogzmc.core.gui.InventoryButton} to add to the inventory GUI.
     * @param slot The slot to place that button at.
     */
    public void addButton(InventoryButton button, Integer slot) {
        inventoryButtons.put(slot, button);
        markForUpdate(slot);
    }

    /**
     *
     * @param button
     * @param slot
     */
    public void moveButton(InventoryButton button, Integer slot) {
        removeButton(button);
        addButton(button, slot);
    }

    /**
     *
     * @param button
     */
    public void markForUpdate(InventoryButton button) {
        markForUpdate(getSlotFor(button));
    }

    /**
     *
     * @param slot
     */
    public void markForUpdate(Integer slot) {
        updatedSlots.add(slot);
    }

    /**
     *
     * @param button
     * @return
     */
    public Integer getSlotFor(InventoryButton button) {
        for (Map.Entry<Integer, InventoryButton> integerInventoryButtonEntry : inventoryButtons.entrySet()) {
            if (integerInventoryButtonEntry.getValue().equals(button)) return integerInventoryButtonEntry.getKey();
        }
        return -1;
    }

    /**
     *
     * @param button
     */
    public void removeButton(InventoryButton button) {
        clearSlot(getSlotFor(button));
    }

    /**
     *
     * @param slot
     */
    public void clearSlot(Integer slot) {
        inventoryButtons.remove(slot);
        markForUpdate(slot);
    }

    /**
     *
     */
    public void updateInventory() {
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
    public void onPlayerLeave(PlayerQuitEvent event) {
        CPlayer onlinePlayer = Core.getOnlinePlayer(event.getPlayer());
        if (observers.contains(onlinePlayer)) this.observers.remove(onlinePlayer);
        Core.logInfo("Removed observer of inventory GUI " + title + " during disconnect. Most likely a timeout!");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        Player player = (Player) event.getPlayer();
        this.observers.remove(Core.getOnlinePlayer(player));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().equals(inventory))) return;
        CPlayer player = Core.getOnlinePlayer((Player) event.getWhoClicked());
        InventoryButton inventoryButton = inventoryButtons.get(event.getSlot());
        if (inventoryButton == null || player == null) throw new IllegalStateException("Somehow, someone who was null clicked on a slot that was null or had no button...");

    }
}
