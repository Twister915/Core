package net.cogzmc.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface Controller {
    ServerInfo getConnectServer(ProxiedPlayer player);
    ServerInfo getFallbackServer(ProxiedPlayer player);
}
