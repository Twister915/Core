package net.cogzmc.hub.modules;

import net.cogzmc.hub.Hub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 5/22/2014
 */
public final class NoWeather implements Listener {
    private boolean noWeather;

    {
        this.noWeather = Hub.getInstance().getConfig().getBoolean("no-weather", true);
    }

    @EventHandler
    public final void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState() && noWeather) event.setCancelled(true);
    }
}
