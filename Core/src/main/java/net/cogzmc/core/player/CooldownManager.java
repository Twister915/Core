package net.cogzmc.core.player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class CooldownManager {
    private final Map<String, Date> cooldownMilliseconds = new HashMap<>();

    public void testCooldown(String key, Long time, TimeUnit unit, Boolean reset) throws CooldownUnexpiredException {
        //Get the last time this cooldown was stored
        Date lastFiredDate = cooldownMilliseconds.get(key);
        //And get now
        Date currentDate = new Date();
        //See how long ago that was in milliseconds
        long millisecondsPassed = currentDate.getTime() - lastFiredDate.getTime();
        //And see how long we're supposed to wait
        long convert = unit.convert(time, TimeUnit.MILLISECONDS);
        //If we're supposed to wait longer than we have
        if (convert >= millisecondsPassed) {
            //The cooldown has yet to expire
            if (reset) this.cooldownMilliseconds.put(key, currentDate);
            throw new CooldownUnexpiredException(unit.convert(convert-millisecondsPassed, TimeUnit.MILLISECONDS), unit);
        }
        this.cooldownMilliseconds.put(key, currentDate);
    }

    public void testCooldown(String key, Long time, TimeUnit unit) throws CooldownUnexpiredException {
        testCooldown(key, time, unit, false);
    }

    public void testCooldown(String key, Long seconds) throws CooldownUnexpiredException {
        testCooldown(key, seconds, TimeUnit.SECONDS);
    }
}
