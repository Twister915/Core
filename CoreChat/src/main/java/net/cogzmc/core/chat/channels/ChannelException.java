package net.cogzmc.core.chat.channels;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ChannelException extends Exception {
    private final String message;
}
