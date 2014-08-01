package net.cogzmc.core.player.fancymessage;

import lombok.*;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ToString
public final class FancyMessage implements Cloneable {
    private final List<FancyMessagePart> parts = new ArrayList<>();

    public static FancyMessagePart start(String message) {
        return new FancyMessagePart(new FancyMessage(), message);
    }

    private void appendPart(FancyMessagePart part) {
        parts.add(part);
    }

    public FancyMessagePart addMore(String message) {
        return new FancyMessagePart(this, message);
    }

    public ImmutableFancyMessage complete() {
        return new ImmutableFancyMessage(this);
    }

    private String getRawMessage() {
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

    @Getter
    public final static class ImmutableFancyMessage implements Cloneable {
        public ImmutableFancyMessage(FancyMessage message) {
            rawMessage = message.getRawMessage();
        }

        private final String rawMessage;

        public void sendTo(Player... players) {
            String rawMessage = getRawMessage();
            for (Player player : players) {
                player.sendRawMessage(rawMessage);
            }
        }

        public void sendTo(CPlayer... players) {
            String rawMessage = getRawMessage();
            for (CPlayer player : players) {
                player.getBukkitPlayer().sendRawMessage(rawMessage);
            }
        }

        public void sendTo(Iterable<CPlayer> players) {
            String rawMessage = getRawMessage();
            for (CPlayer player : players) {
                player.getBukkitPlayer().sendRawMessage(rawMessage);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    @Getter(AccessLevel.PRIVATE)
    public final static class FancyMessagePart {
        @NonNull private final FancyMessage parent;
        @NonNull private final String message;

        private ChatColor color = ChatColor.WHITE;
        private boolean bold = false;
        private boolean italic = false;
        private boolean underline = false;
        private boolean strikethrough = false;
        private boolean obfuscated = false;

        private FHoverAction hoverAction;
        private FClickAction clickAction;

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

        public FHoverAction withHoverAction() {
            return new FHoverAction(this);
        }

        public FancyMessage done() {
            parent.appendPart(this);
            return parent;
        }

        private JSONObject getJSONRepresentation() {
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

    @RequiredArgsConstructor
    public abstract static class FAction {
        protected final FancyMessagePart parent;

        protected abstract String getAction();
        protected abstract String getValue();

        protected abstract void setToParent();

        public FancyMessagePart endAction() {
            if (getAction() == null || getValue() == null) throw new IllegalStateException("You must complete the action!");
            setToParent();
            return parent;
        }
    }

    @Getter
    public static class FHoverAction extends FAction {
        private FHoverActionType type;
        private String value;

        public FHoverAction(FancyMessagePart parent) {
            super(parent);
        }

        public FHoverAction type(FHoverActionType type) {
            this.type = type;
            return this;
        }

        public FHoverAction value(String value) {
            this.value = value;
            return this;
        }

        @Override
        protected String getAction() {
            return type.keyValue;
        }

        @Override
        protected void setToParent() {
            parent.hoverAction = this;
        }
    }

    @Getter
    public static class FClickAction extends FAction {
        private String value;
        private FClickActionType clickActionType;

        public FClickAction(FancyMessagePart parent) {
            super(parent);
        }

        @Override
        protected String getAction() {
            return clickActionType.keyValue;
        }

        @Override
        protected void setToParent() {
            parent.clickAction = this;
        }
    }

    public static enum FHoverActionType {
        SHOW_TEXT("show_text"),
        SHOW_ACHIEVEMENT("show_achievement"),
        SHOW_ITEM("show_item");

        private final String keyValue;

        FHoverActionType(String val) {
            keyValue =  val;
        }

        @Override
        public String toString() {
            return keyValue;
        }
    }

    public static enum FClickActionType {
        OPEN_URL("open_url"),
        OPEN_FILE("open_file"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command");

        private final String keyValue;

        FClickActionType(String val) {
            keyValue =  val;
        }

        @Override
        public String toString() {
            return keyValue;
        }
    }

    private static enum JSONKeys {
        EXTRAS("extras"),
        MESSAGE("text"),
        TRANSLATE("translate"),
        WITH("with"),
        BOLD("bold"),
        ITALIC("italic"),
        UNDERLINE("underlined"),
        STRIKETHROUGH("strikethrough"),
        OBFUSCATED("obfuscated"),
        COLOR("color"),
        CLICK_EVENT("clickEvent"),
        HOVER_EVENT("hoverEvent"),
        ACTION("action"),
        VALUE("value");

        private final String keyValue;

        JSONKeys(String keyValue) {
            this.keyValue = keyValue;
        }

        public String toString() {
            return keyValue;
        }
    }
}
