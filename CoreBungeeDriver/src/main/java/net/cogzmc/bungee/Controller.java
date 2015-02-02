package net.cogzmc.bungee;

public interface Controller {
    ServerInfo getConnectServer(ProxiedPlayer player);
    ServerInfo getFallbackServer(ProxiedPlayer player);
}
