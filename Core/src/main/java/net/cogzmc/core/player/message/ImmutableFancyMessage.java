package net.cogzmc.core.player.message;

import net.cogzmc.core.player.CPlayer;

@Getter
public final class ImmutableFancyMessage implements Cloneable {
    ImmutableFancyMessage(FancyMessage message) {
        rawMessage = message.getRawMessage();
    }

    private final String rawMessage;

    public void sendTo(Player... players) {
        String rawMessage = getRawMessage();
        for (Player player : players) {
            sendToPlayer(player, rawMessage);
        }
    }

    public void sendTo(CPlayer... players) {
        String rawMessage = getRawMessage();
        for (CPlayer player : players) {
            sendToPlayer(player.getBukkitPlayer(), rawMessage);
        }
    }

    public void sendTo(Iterable<CPlayer> players) {
        String rawMessage = getRawMessage();
        for (CPlayer player : players) {
            sendToPlayer(player.getBukkitPlayer(), rawMessage);
        }
    }

    private void sendToPlayer(Player p, String s) {
        WrapperPlayServerChat chat = new WrapperPlayServerChat();
        chat.setMessage(WrappedChatComponent.fromJson(s));
        chat.sendPacket(p);
    }
}
