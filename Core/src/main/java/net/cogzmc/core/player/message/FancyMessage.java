package net.cogzmc.core.player.message;

import java.util.ArrayList;
import java.util.List;

@ToString
public final class FancyMessage implements Cloneable {
    private final List<FancyMessagePart> parts = new ArrayList<>();

    public static FancyMessagePart start(String message) {
        return new FancyMessagePart(new FancyMessage(), message);
    }

    void appendPart(FancyMessagePart part) {
        parts.add(part);
    }

    public FancyMessagePart addMore(String message) {
        return new FancyMessagePart(this, message);
    }

    public ImmutableFancyMessage complete() {
        return new ImmutableFancyMessage(this);
    }

    String getRawMessage() {
        if (parts.size() < 1) throw new IllegalStateException("You cannot send a message that you have not completed!");
        FancyMessagePart rootPart = parts.get(0);
        JSONObject root = rootPart.getJSONRepresentation();
        if (parts.size() > 1) {
            JSONArray extras = new JSONArray();
            for (int i = 1; i < parts.size(); i++) {
                FancyMessagePart fancyMessagePart = parts.get(i);
                extras.add(fancyMessagePart.getJSONRepresentation());
            }
            root.put(JSONKeys.EXTRAS, extras);
        }
        return root.toJSONString();
    }

}
