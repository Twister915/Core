package net.cogzmc.core.player.message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class FAction {
    protected abstract String getAction();
    protected abstract String getValue();
}
