package net.communitycraft.core.player;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CPlayerJoinException extends Exception {
    private final String disconectMessage;
    private boolean disconnect;
}
