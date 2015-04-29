package net.cogzmc.core.json;

import org.json.simple.JSONObject;

@Deprecated
/**
 * @deprecated Use GSON
 */
public interface JSONSerializer<T> {
    public JSONObject serialize(T object);
    public T deserialize(JSONObject object);
}
