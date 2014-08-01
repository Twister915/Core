package net.cogzmc.core.player.fancymessage;

import lombok.Getter;

@Getter
public class FHoverAction extends FancyMessage.FAction {
    private FHoverActionType type;
    private String value;

    public FHoverAction(FancyMessage.FancyMessagePart parent) {
        super(parent);
    }

    public FHoverAction type(FHoverActionType type) {
        this.type = type;
        return this;
    }

    public FHoverAction value(String value) {
        this.value = value;
        return this;
    }

    @Override
    protected String getAction() {
        return type.toString();
    }

    @Override
    protected void setToParent() {
        parent.setHoverAction(this);
    }
}
