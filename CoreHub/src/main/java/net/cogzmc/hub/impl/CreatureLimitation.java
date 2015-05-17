package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public final class CreatureLimitation extends Limitation {
    public CreatureLimitation() {
        super("no-creatures");
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.getEntity().remove();
    }

    @Override
    protected void onRegister() {
        for (World world : Bukkit.getWorlds())
            world.getEntitiesByClass(LivingEntity.class).stream().filter((e) -> e instanceof Player).forEach(LivingEntity::remove);
    }
}
