package net.communitycraft.core.player;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = false)
@Data
public final class CooldownUnexpiredException extends Exception {
    private final Long timeRemaining;
    private final TimeUnit timeUnit;
}
