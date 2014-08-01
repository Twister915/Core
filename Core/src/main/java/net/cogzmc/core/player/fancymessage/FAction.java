package net.cogzmc.core.player.fancymessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class FAction {
    protected final FancyMessage.FancyMessagePart parent;

    protected abstract String getAction();
    protected abstract String getValue();

    protected abstract void setToParent();

    public FancyMessage.FancyMessagePart endAction() {
        if (getAction() == null || getValue() == null) throw new IllegalStateException("You must complete the action!");
        setToParent();
        return parent;
    }
}
