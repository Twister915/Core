package net.cogzmc.core.json;

import net.cogzmc.core.util.Point;
import net.cogzmc.core.util.Region;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public final class RegionSerializer implements JSONSerializer<Region> {
    private final static String MIN = "min";
    private final static String MAX = "max";

    @Override
    public JSONObject serialize(Region object) {
        PointSerializer serializer = Point.getSerializer();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MIN, serializer.serialize(object.getMin()));
        jsonObject.put(MAX, serializer.serialize(object.getMax()));
        return jsonObject;
    }

    @Override
    public Region deserialize(JSONObject object) {
        PointSerializer serializer = Point.getSerializer();
        return new Region(serializer.deserialize((JSONObject) object.get(MIN)), serializer.deserialize((JSONObject) object.get(MAX)));
    }
}
