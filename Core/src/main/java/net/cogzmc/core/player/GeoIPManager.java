package net.cogzmc.core.player;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Data
public final class GeoIPManager {
    private final File dbFile;
    private final DatabaseReader databaseReader;

    public GeoIPManager(@NonNull File dbFile) throws IOException {
        this.dbFile = dbFile;
        this.databaseReader = new DatabaseReader.Builder(dbFile).build();
    }

    public GeoIPInfo getInfoOn(InetAddress address) {
        CityResponse city;
        try {
            city = this.databaseReader.city(address);
        } catch (GeoIp2Exception | IOException e) {
            return null;
        }
        return new GeoIPInfo(address, city);
    }

    @Value
    public static class GeoIPInfo {
        private InetAddress address;
        private CityResponse response;
    }
}
