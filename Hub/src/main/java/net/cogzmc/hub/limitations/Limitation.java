package net.cogzmc.hub.limitations;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import net.cogzmc.hub.Hub;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

@Data
@EqualsAndHashCode(of = {"configKey"})
public abstract class Limitation implements Listener {
    private final String configKey;
    @Setter(AccessLevel.NONE) private boolean enabled;

    public void enable() throws LimitationNotRequiredException {
        if (!Hub.getInstance().getConfig().getBoolean(configKey, false)) throw new LimitationNotRequiredException();
        Hub.getInstance().registerListener(this);
        onRegister();
        enabled = true;
    }

    protected void onRegister() {}
    protected final boolean shouldIgnoreLimitation(Player player) {return player.isOp() || player.hasPermission("hub.ignorelimits");}
    protected final boolean shouldIgnoreLimitation(PlayerEvent event) {return shouldIgnoreLimitation(event.getPlayer());}
}
