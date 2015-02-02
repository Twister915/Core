package net.cogzmc.core.json;

import org.json.simple.JSONObject;

public interface JSONSerializer<T> {
    public JSONObject serialize(T object);
    public T deserialize(JSONObject object);
}
