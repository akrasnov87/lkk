package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.configuration.ConfigurationSetting;
import ru.mobnius.simple_core.data.configuration.ConfigurationSettingUtil;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.preferences.GeneralPreferences;

/**
 * Класс загрузки конфигураций с сервера
 * в дополнительном потоке
 */
public class ConfigTask implements Callable<Boolean> {

    @NonNull
    @Override
    public Boolean call() throws Exception {
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return false;
        }
        if (Authorization.getInstance().isAuthorized()) {
            final BasicCredentials credentials = Authorization.getInstance().user.getCredentials();

            try {
                final List<ConfigurationSetting> configurationSettings = ConfigurationSettingUtil.getSettings(GlobalSettings.getConnectUrl(), credentials);
                if (configurationSettings == null) {
                    return false;
                }
                if (GeneralPreferences.getInstance() == null) {
                    return false;
                }
                return GeneralPreferences.getInstance().updateSettings(configurationSettings);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
