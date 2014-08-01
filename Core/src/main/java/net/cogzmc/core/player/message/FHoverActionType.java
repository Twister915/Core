package net.cogzmc.core.player.message;

public enum FHoverActionType {
    SHOW_TEXT("show_text"),
    SHOW_ACHIEVEMENT("show_achievement"),
    SHOW_ITEM("show_item");

    private final String keyValue;

    FHoverActionType(String val) {
        keyValue =  val;
    }

    @Override
    public String toString() {
        return keyValue;
    }
}
