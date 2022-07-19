package ru.mobnius.simple_core.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import ru.mobnius.simple_core.data.configuration.ConfigurationSetting;
import ru.mobnius.simple_core.utils.IntUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class GeneralPreferences {

    private static final String GENERAL_PREFERENCES_NAME = "ru.mobnius.simple_core.preferences.GENERAL_PREFERENCES_NAME";
    private static final String GENERAL_PREFERENCES_TEST_NAME = "ru.mobnius.simple_core.preferences.GENERAL_PREFERENCES_TEST_NAME";

    private static final String SINGLE_USER_KEY = "ru.mobnius.simple_core.preferences.SINGLE_USER_KEY";
    private static final String IS_NOTIFIED_ABOUT_BATTERY_SAVE_MODE = "ru.mobnius.simple_core.preferences.IS_NOTIFIED_ABOUT_BATTERY_SAVE_MODE";
    private static final String SERVER_URL = "ru.mobnius.simple_core.preferences.SERVER_URL";
    private static final String ENVIROMENT = "ru.mobnius.simple_core.preferences.ENVIROMENT";

    @Nullable
    private static GeneralPreferences preferencesManager;
    @Nullable
    private static SharedPreferences sharedPreferences;

    @Nullable
    public static GeneralPreferences getInstance() {
        return preferencesManager;
    }


    public static GeneralPreferences createInstance(final @NonNull Context context) {
        return preferencesManager = new GeneralPreferences(context, GENERAL_PREFERENCES_NAME);
    }

    @VisibleForTesting
    public static GeneralPreferences createTestInstance(final @NonNull Context context) {
        return preferencesManager = new GeneralPreferences(context, GENERAL_PREFERENCES_TEST_NAME);
    }

    private GeneralPreferences(final @NonNull Context context, final @NonNull String name) {
        try {
            final MasterKey mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(context,
                    GENERAL_PREFERENCES_NAME, mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
    }

    public boolean isSingleUser() {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(SINGLE_USER_KEY, false);
    }

    public void setIsSingleUser(final boolean value) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putBoolean(SINGLE_USER_KEY, value).apply();
    }

    public boolean isNotifiedAboutBatterySave() {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(IS_NOTIFIED_ABOUT_BATTERY_SAVE_MODE, false);
    }

    public void setNotifiedAboutBatterySave(final boolean value) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putBoolean(IS_NOTIFIED_ABOUT_BATTERY_SAVE_MODE, value).apply();
    }

    public void setServerUrl(final @NonNull String serverUrl) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putString(SERVER_URL, serverUrl).apply();
    }

    @NonNull
    public String getServerUrl() {
        if (sharedPreferences == null) {
            return StringUtil.EMPTY;
        }
        return StringUtil.defaultEmptyString(sharedPreferences.getString(SERVER_URL, StringUtil.EMPTY));
    }
    public void setEnviroment(final @NonNull String enviroment) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putString(ENVIROMENT, enviroment).apply();
    }

    @NonNull
    public String getEnviroment() {
        if (sharedPreferences == null) {
            return StringUtil.EMPTY;
        }
        return StringUtil.defaultEmptyString(sharedPreferences.getString(ENVIROMENT, StringUtil.EMPTY));
    }

    /**
     * обновление настроек
     *
     * @param configurationSettings массив настроке
     * @return true - настройки обновлены
     */
    public boolean updateSettings(final @NonNull List<ConfigurationSetting> configurationSettings) {
        if (sharedPreferences == null) {
            return false;
        }
        boolean refresh = false;
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        for (ConfigurationSetting configurationSetting : configurationSettings) {
            try {
                if (ConfigurationSetting.INTEGER.equals(configurationSetting.type)) {
                    if (isUpdateInt(configurationSetting.key, IntUtil.getIntOrMinus(configurationSetting.value))) {
                        editor.putInt(configurationSetting.key, IntUtil.getIntOrMinus(configurationSetting.value));
                        refresh = true;
                    }
                } else if (ConfigurationSetting.BOOLEAN.equals(configurationSetting.type)) {
                    if (isUpdateBoolean(configurationSetting.key, Boolean.parseBoolean(configurationSetting.value))) {
                        editor.putBoolean(configurationSetting.key, Boolean.parseBoolean(configurationSetting.value));
                        refresh = true;
                    }
                } else {
                    if (isUpdateString(configurationSetting.key, configurationSetting.value)) {
                        editor.putString(configurationSetting.key, configurationSetting.value);
                        refresh = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        editor.apply();
        return refresh;
    }

    protected boolean isUpdateInt(final @Nullable String key, final int value) {
        if (key == null) {
            return false;
        }
        final int intValue = getIntValue(key, Integer.MIN_VALUE);
        return intValue != value;
    }

    protected boolean isUpdateBoolean(final @Nullable String key, final boolean value) {
        if (key == null) {
            return false;
        }
        final boolean booleanValue = getBooleanValue(key, false);
        return booleanValue != value;
    }

    protected boolean isUpdateString(final @Nullable String key, final @Nullable String value) {
        if (key == null) {
            return false;
        }
        final String stringValue = getStringValue(key, null);
        return StringUtil.isEmpty(stringValue) || !stringValue.equals(value);
    }

    /**
     * Получение настройки
     *
     * @param key          ключ настройки
     * @param defaultValue значение по умолчанию
     * @return значение
     */
    public @NonNull
    String getStringValue(final @NonNull String key, final @Nullable String defaultValue) {
        if (sharedPreferences == null) {
            return StringUtil.EMPTY;
        }
        final String value = sharedPreferences.getString(key, defaultValue);
        if (StringUtil.isEmpty(value)) {
            return StringUtil.EMPTY;
        }
        return value;
    }

    /**
     * Получение настройки
     *
     * @param key          ключ настройки
     * @param defaultValue значение по умолчанию
     * @return значение
     */
    public boolean getBooleanValue(final @NonNull String key, final boolean defaultValue) {
        if (sharedPreferences == null) {
            return defaultValue;
        }
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Получение настройки
     *
     * @param key          ключ настройки
     * @param defaultValue значение по умолчанию
     * @return значение
     */
    public int getIntValue(final @NonNull String key, final int defaultValue) {
        if (sharedPreferences == null) {
            return defaultValue;
        }
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Очистка настроек
     */
    public void clear() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }

    public void destroy() {
        sharedPreferences = null;
    }

}
