package net.cogzmc.core.json;

public interface JSONSerializer<T> {
    public JSONObject serialize(T object);
    public T deserialize(JSONObject object);
}
