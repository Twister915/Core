package net.communitycraft.core.model.mongo;

public enum MongoModelKeys {
    EMBEDDED_VALUE_TYPE("element_type"),
    EMBEDDED_MAP_FLAG("map"),
    EMBEDDED_LIST_FLAG("list"),
    EMBEDDED_FLAG_KEY("type_flag"),
    EMBEDDED_CONTENTS_KEY("contents");
    private final String keyValue;

    MongoModelKeys(String key) {
        keyValue = key;
    }

    public String toString() {
        return keyValue;
    }
}
