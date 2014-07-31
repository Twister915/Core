package net.cogzmc.core.test.tests.mobs;

import net.cogzmc.core.Core;
import net.cogzmc.core.test.TestModule;
import net.cogzmc.core.test.tests.ITest;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class CaryTest implements ITest, Listener {
    @Override
    public void onEnable() {
        TestModule.getInstance().registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("tests.cary")) return;
        Bukkit.getScheduler().runTaskLater(TestModule.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerTrackingMobTest test = new PlayerTrackingMobTest(Core.getOnlinePlayer(event.getPlayer()));
                test.init();
            }
        }, 1L);
    }
}
