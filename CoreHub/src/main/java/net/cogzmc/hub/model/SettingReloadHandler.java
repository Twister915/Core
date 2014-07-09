package net.cogzmc.hub.model;

import lombok.Data;
import lombok.NonNull;
import net.cogzmc.core.network.NetCommandHandler;
import net.cogzmc.core.network.NetworkServer;

@Data
public final class SettingReloadHandler implements NetCommandHandler<SettingReloadNetCommand> {
    @NonNull private final SettingsManager manager;

    @Override
    public void handleNetCommand(NetworkServer sender, SettingReloadNetCommand netCommand) {
        manager.reload();
    }
}
