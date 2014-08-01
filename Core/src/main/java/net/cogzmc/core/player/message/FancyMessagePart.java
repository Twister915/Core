package net.cogzmc.core.player.message;

import lombok.*;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public final class FancyMessagePart {
    @NonNull private final FancyMessage parent;
    @NonNull private final String message;

    private ChatColor color = ChatColor.WHITE;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private boolean strikethrough = false;
    private boolean obfuscated = false;

    @Setter(AccessLevel.PACKAGE) private FHoverAction hoverAction;
    @Setter(AccessLevel.PACKAGE) private FClickAction clickAction;

    public FancyMessagePart obfuscate() {
        obfuscated = !obfuscated;
        return this;
    }

    public FancyMessagePart bold() {
        bold = !bold;
        return this;
    }

    public FancyMessagePart underline() {
        underline = !underline;
        return this;
    }

    public FancyMessagePart italic() {
        italic = !italic;
        return this;
    }

    public FancyMessagePart strikethrough() {
        strikethrough = !strikethrough;
        return this;
    }

    public FancyMessagePart color(@NonNull ChatColor color) {
        this.color = color;
        return this;
    }

    public FancyMessagePart withHoverAction(FHoverActionType type, String value) {
        hoverAction = new FHoverAction(type, value);
        return this;
    }

    public FancyMessagePart withClickAction(FClickActionType type, String value) {
        clickAction = new FClickAction(value, type);
        return this;
    }

    public FancyMessage done() {
        parent.appendPart(this);
        return parent;
    }

    JSONObject getJSONRepresentation() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.MESSAGE, message);
        jsonObject.put(JSONKeys.COLOR, (color == null ? ChatColor.WHITE : color).name().toLowerCase());
        if (bold) jsonObject.put(JSONKeys.BOLD, true);
        if (italic) jsonObject.put(JSONKeys.ITALIC, true);
        if (strikethrough) jsonObject.put(JSONKeys.STRIKETHROUGH, true);
        if (obfuscated) jsonObject.put(JSONKeys.OBFUSCATED, true);
        if (hoverAction != null) jsonObject.put(JSONKeys.HOVER_EVENT, encodeAction(hoverAction));
        if (clickAction != null) jsonObject.put(JSONKeys.CLICK_EVENT, encodeAction(clickAction));
        return jsonObject;
    }

    private JSONObject encodeAction(@NonNull FAction action) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.ACTION, action.getAction());
        jsonObject.put(JSONKeys.VALUE, action.getValue());
        return jsonObject;
    }
}
