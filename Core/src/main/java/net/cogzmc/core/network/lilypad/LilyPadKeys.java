package net.cogzmc.core.network.lilypad;

enum LilyPadKeys {
    NET_COMMAND_CLASS_NAME("class"),
    NET_COMMAND_ARGUMENTS("args"),
    NET_COMMAND_TIME("time_sent");
    private final String value;

    LilyPadKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
