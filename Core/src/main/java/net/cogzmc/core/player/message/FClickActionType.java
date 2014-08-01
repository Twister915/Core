package net.cogzmc.core.player.message;

public enum FClickActionType {
    OPEN_URL("open_url"),
    OPEN_FILE("open_file"),
    RUN_COMMAND("run_command"),
    SUGGEST_COMMAND("suggest_command");

    private final String keyValue;

    FClickActionType(String val) {
        keyValue =  val;
    }

    @Override
    public String toString() {
        return keyValue;
    }
}
