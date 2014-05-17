package net.communitycraft.core.entities;

import net.communitycraft.core.player.CPlayer;
import org.bukkit.event.block.Action;

public interface EntityObserver<ObservedType extends FakeEntity> {
    void entityInteractedWith(ObservedType entity, CPlayer player, Action action);
}
