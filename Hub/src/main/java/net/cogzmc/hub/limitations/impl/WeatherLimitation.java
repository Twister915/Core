package net.cogzmc.hub.limitations.impl;

import lombok.EqualsAndHashCode;
import net.cogzmc.hub.limitations.Limitation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

@EqualsAndHashCode(callSuper = true)
public final class WeatherLimitation extends Limitation {
    public WeatherLimitation() {
        super("weather");
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        //TODO check if that's the right weather state
        if (event.toWeatherState()) event.setCancelled(true);
    }
}
