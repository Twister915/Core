package net.cogzmc.hub.model;

import lombok.NoArgsConstructor;
import net.cogzmc.core.network.NetCommand;

/**
 * Sent to notify other servers to reload their settings
 */
@NoArgsConstructor
public final class SettingReloadNetCommand implements NetCommand {}
