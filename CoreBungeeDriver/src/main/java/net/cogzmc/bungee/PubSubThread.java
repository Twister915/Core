package net.cogzmc.bungee;

import lombok.Data;

@Data
public class PubSubThread extends Thread {
    private final BasePubSub pubSub;

    @Override
    public void run() {
        CoreBungeeDriver.getInstance().getJedisClient().subscribe(pubSub, pubSub.getChannel());
    }
}
