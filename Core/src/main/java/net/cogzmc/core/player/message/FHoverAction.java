package net.cogzmc.core.player.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public final class FHoverAction extends FAction {
    private final FHoverActionType type;
    private final String value;

    @Override
    protected String getAction() {
        return type.toString();
    }
}
