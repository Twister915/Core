package net.cogzmc.core.player;

import net.cogzmc.core.modular.command.CommandException;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = false)
@Data
public final class CooldownUnexpiredException extends CommandException {
    private final Long timeRemaining;
    private final TimeUnit timeUnit;

    public CooldownUnexpiredException(Long timeRemaining, TimeUnit timeUnit) {
        super("Unexpired cooldown");
        this.timeRemaining = timeRemaining;
        this.timeUnit = timeUnit;
    }
}
