package net.cogzmc.bungee;

@EqualsAndHashCode(callSuper = false)
@Data
class PubSubThread extends Thread {
    private final BasePubSub pubSub;

    @Override
    public void run() {
        CoreBungeeDriver.getInstance().getJedisClient().subscribe(pubSub, pubSub.getChannel());
    }
}
