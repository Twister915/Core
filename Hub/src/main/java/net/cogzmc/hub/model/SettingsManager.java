package net.cogzmc.hub.model;

import lombok.SneakyThrows;
import net.cogzmc.core.Core;
import net.cogzmc.core.model.ModelStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * In an effort to provide easy change between one hub and many, it is best practice to store
 * any information that would need to be synced across multiple hubs in the database. This system
 * attempts to provide a method of storing "settings" defined by the {@link net.cogzmc.hub.model.Setting} enum
 * in the database, and also getting their values.
 *
 * To store settings see the {@link #setSettingValue(Setting, Object)} method.
 * To get the value of settings see the {@link #getSettingValueFor(Setting, Class)} method.
 * To save the current stored values into the database, see the {@link #save()} method.
 * To reload the current values from the database, see the {@link #reload()} method delegated from {@link net.cogzmc.core.model.ModelStorage}.
 *
 */
public final class SettingsManager {
    private final Map<Setting, HubSetting<?>> settingsMap = new HashMap<>();
    private final ModelStorage<HubSetting> modelStorage = Core.getModelManager().getModelStorage(HubSetting.class);

    /**
     * Loads current setting entries into the settingsMap and gets values from the database.
     */
    public SettingsManager() {
        Core.getNetworkManager().registerNetCommandHandler(new SettingReloadHandler(this), SettingReloadNetCommand.class);
        reload(); //Reload the values.
    }

    /**
     * Gets the value of a setting from the cached values.
     *
     * To reload the states of these values use {@link #reload()}.
     *
     * Returns {@code null} if the types cannot be cast
     * @param setting The setting to get the value for.
     * @param explicitType The type to cast to.
     * @param <T> Type parameter.
     * @return The value of this setting specified by the param {@code setting}
     */
    public <T> T getSettingValueFor(Setting setting, @SuppressWarnings("UnusedParameters") Class<T> explicitType) {
        try {
            //noinspection unchecked
            return (T) this.settingsMap.get(setting).getValue();
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Saves a value in the settings database and sends a mass command to reload all hub server settings.
     * @param setting The setting to set the value for.
     * @param value The value to set the setting to.
     */
    @SneakyThrows
    public void setSettingValue(Setting setting, Object value) {
        HubSetting<?> hubSetting = this.settingsMap.get(setting);
        hubSetting.setValue(value);
        if (Core.getNetworkManager() != null) Core.getNetworkManager().sendMassNetCommand(new SettingReloadNetCommand());
        modelStorage.saveValue(hubSetting);
    }

    /**
     *
     */
    @SneakyThrows
    public void save() {
        for (Map.Entry<Setting, HubSetting<?>> settingHubSettingEntry : settingsMap.entrySet()) {
            modelStorage.saveValue(settingHubSettingEntry.getValue());
        }
    }

    /**
     * Reloads the values for the settings based on the storage class.
     * @param databaseReload If the storage should grab new values from the database.
     */
    @SneakyThrows
    public void reload(boolean databaseReload) {
        if (databaseReload) modelStorage.reload();
        for (Setting setting : Setting.values()) {
            HubSetting settingModel = modelStorage.findValue("key", setting.getKey());
            if (settingModel == null) {
                settingModel = new HubSetting();
                settingModel.setKey(setting.getKey());
            }
            settingsMap.put(setting, settingModel);
        }
    }

    /**
     *
     */
    public void reload() {
        reload(true);
    }
}
