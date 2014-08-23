package net.cogzmc.bungee;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
class PubSubThread extends Thread {
    private final BasePubSub pubSub;

    @Override
    public void run() {
        CoreBungeeDriver.getInstance().getJedisClient().subscribe(pubSub, pubSub.getChannel());
    }
}
