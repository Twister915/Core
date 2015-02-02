package net.cogzmc.hub;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.bukkit.entity.Player;

@Data
@EqualsAndHashCode(of = {"configKey"})
public abstract class Limitation implements Listener {
    @NonNull private final String configKey;
    @Setter(AccessLevel.NONE) private boolean enabled = false;

    public void enable() throws LimitationNotRequiredException {
        if (!Hub.getInstance().getConfig().getBoolean("limitations." + configKey, false)) throw new LimitationNotRequiredException();
        Hub.getInstance().registerListener(this);
        onRegister();
        enabled = true;
    }

    protected void onRegister() {}
    protected final boolean shouldIgnoreLimitation(Player player) {return player.isOp() || player.hasPermission("hub.ignorelimits");}
    protected final boolean shouldIgnoreLimitation(PlayerEvent event) {return shouldIgnoreLimitation(event.getPlayer());}
}
