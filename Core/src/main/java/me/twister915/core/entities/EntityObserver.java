package me.twister915.core.entities;

import me.twister915.core.player.CPlayer;
import org.bukkit.event.block.Action;

public interface EntityObserver<ObservedType extends FakeEntity> {
    void entityInteractedWith(ObservedType entity, CPlayer player, Action action);
}
