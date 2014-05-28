package net.cogzmc.core.chat.channels;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ChannelException extends Exception {
    private final String message;
}
