package net.cogzmc.gameapi.model.game;

@Data
@Setter(AccessLevel.NONE)
public final class GameCountdown implements Runnable {
    private final Integer seconds;
    private final Game<?> game;
    private final GameCountdownDelegate delegate;

    private Integer timeElapsed;
    @Getter(AccessLevel.NONE) private BukkitTask taskTimer;

    public void start() {
        taskTimer = Bukkit.getScheduler().runTaskTimer(game.getOwner(), this, 1L, 1L);
        game.gameCountdownStarted(this);
        delegate.countdownStarted(this, seconds);
    }

    public void pause() {
        taskTimer.cancel();
    }

    public void stopAndReset() {
        pause();
        delegate.countdownEnded(this, seconds);
        game.gameCountdownEnded(this);
        timeElapsed = 0;
    }

    @Override
    public void run() {
        timeElapsed++;
        delegate.countdownChanged(this, seconds-timeElapsed, seconds);
        if (timeElapsed.equals(seconds)) {
            stopAndReset();
        }
    }
}
