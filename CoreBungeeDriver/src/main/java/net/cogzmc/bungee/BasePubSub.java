package net.cogzmc.bungee;

import redis.clients.jedis.JedisPubSub;

public abstract class BasePubSub extends JedisPubSub {
    private final String channel;

    protected BasePubSub(String chan) {
        this.channel = chan;
        CoreBungeeDriver.getInstance().getJedisClient().subscribe(this, chan);
    }

    @Override
    public void onMessage(String s, String s2) {

    }

    @Override
    public void onPMessage(String s, String s2, String s3) {

    }

    @Override
    public void onSubscribe(String s, int i) {

    }

    @Override
    public void onUnsubscribe(String s, int i) {

    }

    @Override
    public void onPUnsubscribe(String s, int i) {

    }

    @Override
    public void onPSubscribe(String s, int i) {

    }
}
