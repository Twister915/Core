package net.cogzmc.gameapi.model.game.countdown;

public interface GameCountdownDelegate {
    void countdownStarted(GameCountdown countdown, Integer secondsRemaining);
    void countdownChanged(GameCountdown countdown, Integer secondsRemaining, Integer maxSeconds);
    void countdownEnded(GameCountdown countdown, Integer maxSeconds);
}
