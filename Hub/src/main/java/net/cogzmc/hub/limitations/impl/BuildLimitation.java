package net.cogzmc.hub.limitations.impl;

import lombok.EqualsAndHashCode;
import net.cogzmc.hub.limitations.Limitation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@EqualsAndHashCode(callSuper = true)
public final class BuildLimitation extends Limitation {
    public BuildLimitation() {
        super("no-build");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (shouldIgnoreLimitation(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        if (shouldIgnoreLimitation(event)) return;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return;
        }
        switch (event.getClickedBlock().getType()) {
            case TRAP_DOOR:
            case TRAPPED_CHEST:
            case CHEST:
            case IRON_DOOR:
            case WOOD_DOOR:
            case ANVIL:
            case BED:
            case BED_BLOCK:
            case BEACON:
            case BREWING_STAND:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case LEVER:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case BURNING_FURNACE:
            case FURNACE:
            case WORKBENCH:
            case CAULDRON:
            case ENCHANTMENT_TABLE:
            case NOTE_BLOCK:
            case JUKEBOX:
            case CAKE_BLOCK:
            case ENDER_PORTAL_FRAME:
                break;
            default:
                return;
        }
        event.setCancelled(true);
    }
}