package me.joeyandtom.communitycraft.core.player.mongo;

import lombok.Data;
import lombok.NonNull;
import me.joeyandtom.communitycraft.core.asset.Asset;
import me.joeyandtom.communitycraft.core.player.COfflinePlayer;
import me.joeyandtom.communitycraft.core.player.CPlayer;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class COfflineMongoPlayer implements COfflinePlayer {
    private final List<String> knownUsernames;
    private final String lastKnownUsername;
    private final UUID uniqueIdentifier;
    private final List<String> knownIPAddresses;
    private final Date firstTimeOnline;
    private final Date lastTimeOnline;
    private final Long millisecondsOnline;

    @Override
    public <T> T getSettingValue(@NonNull String key, @NonNull Class<T> type, T defaultValue) {
        return null;
    }

    @Override
    public <T> T getSettingValue(@NonNull String key, @NonNull Class<T> type) {
        return null;
    }

    @Override
    public void storeSettingValue(@NonNull String key, Object value) {

    }

    @Override
    public void removeSettingValue(@NonNull String key) {

    }

    @Override
    public boolean isSettingValuePresent(@NonNull String key) {
        return false;
    }

    @Override
    public void giveAsset(@NonNull Asset asset) {

    }

    @Override
    public Collection<Asset> getAssets() {
        return null;
    }

    @Override
    public boolean hasAsset(Asset asset) {
        return false;
    }

    @Override
    public Asset getAssetByName(String key) {
        return null;
    }

    @Override
    public CPlayer getPlayer() {
        return null;
    }

    @Override
    public void updateFromDatabase() {

    }

    @Override
    public void saveIntoDatabase() {

    }
}
