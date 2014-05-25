package net.cogzmc.core.player.mongo;

/**
 *
 */
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
    ASSETS_KEY("assets"),
    USER_GROUPS_KEY("groups"),
    GROUPS_COLLECTION("groups"),
    GROUPS_NAME_KEY("name"),
    GROUPS_TABLIST_COLOR_KEY("tablist_color"),
    GROUPS_CHAT_COLOR_KEY("chatcolor_color"),
    GROUPS_CHAT_PREFIX_KEY("chat_prefix"),
    GROUPS_PARENTS_KEY("parents"),
    GROUPS_PERMISSIONS_KEY("permissions"),
    GROUPS_DEFAULT_MARKER("default_marker"),
    GROUPS_PRIORITY_KEY("priority"),
    GROUPS_CHAT_SUFFIX_KEY("suffix"),
    PERMISSION_PERM("permission"),
    PERMISSION_VALUE("value");
    private final String value;
    MongoKey(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
