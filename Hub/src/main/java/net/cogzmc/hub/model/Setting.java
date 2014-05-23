package net.cogzmc.hub.model;

/**
 * Add an entry to this enum to add a setting to store.
 */
public enum Setting {
    SPAWN("spawn");
    private final String key;

    Setting(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
