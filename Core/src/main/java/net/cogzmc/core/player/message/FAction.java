package net.cogzmc.core.player.message;

@RequiredArgsConstructor
public abstract class FAction {
    protected abstract String getAction();
    protected abstract String getValue();
}
