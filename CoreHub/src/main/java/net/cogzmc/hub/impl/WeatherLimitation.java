package net.cogzmc.hub.impl;

import net.cogzmc.hub.Limitation;

@EqualsAndHashCode(callSuper = true)
public final class WeatherLimitation extends Limitation {
    public WeatherLimitation() {
        super("no-weather");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        //TODO check if that's the right weather state
        if (event.toWeatherState()) event.setCancelled(true);
    }
}
