package net.cogzmc.core.player.fancymessage;

import lombok.Getter;

@Getter
public class FClickAction extends FancyMessage.FAction {
    private String value;
    private FClickActionType clickActionType;

    public FClickAction(FancyMessage.FancyMessagePart parent) {
        super(parent);
    }

    @Override
    protected String getAction() {
        return clickActionType.toString();
    }

    @Override
    protected void setToParent() {
        parent.setClickAction(this);
    }
}
