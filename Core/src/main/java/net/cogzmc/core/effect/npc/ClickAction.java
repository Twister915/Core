package net.cogzmc.core.effect.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;

public enum ClickAction {
    RIGHT_CLICK,
    LEFT_CLICK;

    public static ClickAction valueOf(EnumWrappers.EntityUseAction action) {
        switch (action) {
            case INTERACT:
                return RIGHT_CLICK;
            case ATTACK:
                return LEFT_CLICK;
        }
        return null;
    }
}
