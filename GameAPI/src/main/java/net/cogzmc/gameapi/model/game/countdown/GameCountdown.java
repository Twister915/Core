package net.cogzmc.gameapi.model.game.countdown;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import net.cogzmc.gameapi.model.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@Value
public final class GameCountdown implements Runnable {
    private final Integer seconds;
    private final Game<?> game;
    private final GameCountdownDelegate delegate;

    private Integer timeElapsed;
    @Getter(AccessLevel.NONE) private BukkitTask taskTimer;

    public void start() {
        taskTimer = Bukkit.getScheduler().runTaskTimer(game.getOwner(), this, 1L, 1L);
        delegate.countdownStarted(this, seconds);
    }

    public void pause() {
        taskTimer.cancel();
    }

    public void stopAndReset() {
        pause();
        timeElapsed = 0;
    }

    @Override
    public void run() {
        timeElapsed++;
        delegate.countdownChanged(this, seconds-timeElapsed, seconds);
        if (timeElapsed.equals(seconds)) {
            delegate.countdownEnded(this, seconds);
            taskTimer.cancel();
        }
    }
}
