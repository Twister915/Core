package net.cogzmc.core.player.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public final class FClickAction extends FAction {
    private final String value;
    private final FClickActionType clickActionType;

    @Override
    protected String getAction() {
        return clickActionType.toString();
    }
}
