package net.cogzmc.core.player.fancymessage;

import lombok.Getter;
import net.cogzmc.core.player.CPlayer;
import org.bukkit.entity.Player;

@Getter
public final class ImmutableFancyMessage implements Cloneable {
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
