package net.cogzmc.core.network;

public interface NetworkServerDiscoverObserver {
    void onNetworkServerDiscover(NetworkServer server);
    void onNetworkServerRemove(NetworkServer remove);
}
