package net.cogzmc.core.effect.npc;

import net.cogzmc.core.player.CPlayer;

/**
 * Observer for the {@link net.cogzmc.core.effect.npc.mobs.MobNPCVillager} villager.
 */
public interface NPCObserver {
    /**
     * Called when a player interacts with the entity.
     * @param player The player whom interacted with the entity.
     * @param villager The villager that was interacted with.
     * @param action How they interacted with the entity.
     */
    void onPlayerInteract(CPlayer player, AbstractMobNPC villager, ClickAction action);
}
