package net.communitycraft.core.player.mongo;

public enum MongoKey {
    USERS_COLLETION("users"),
    UUID_KEY("uuid"),
    USERNAMES_KEY("usernames"),
    ID_KEY("_id"),
    LAST_USERNAME_KEY("last_username"),
    FIRST_JOIN_KEY("first_join"),
    LAST_SEEN_KEY("last_seen"),
    TIME_ONLINE_KEY("time_online"),
    IPS_KEY("ips"),
    SETTINGS_KEY("settings"),
    FULLY_QUALIFIED_CLASS_NAME_KEY("fqcn"),
    META_KEY("meta"),
    ASSETS_KEY("assets");
    private final String value;
    MongoKey(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
