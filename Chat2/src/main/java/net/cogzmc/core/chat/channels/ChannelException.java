package net.cogzmc.core.chat.channels;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelException extends Exception {
    private final String message;
}
