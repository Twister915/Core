package net.cogzmc.core.entities;

import net.cogzmc.core.player.CPlayer;
import org.bukkit.event.block.Action;

public interface EntityObserver<ObservedType extends FakeEntity> {
    void entityInteractedWith(ObservedType entity, CPlayer player, Action action);
}
