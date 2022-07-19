package ru.mobnius.simple_core.data.configuration;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ru.mobnius.simple_core.utils.StringUtil;

public class PreferencesManager {

    public final static String MBL_ZIP = "MBL_ZIP";
    public final static String SYNC_PROTOCOL_v2 = "v2";
    public static final String DEBUG = "MBL_DEBUG";
    public static final String MBL_LOCATION = "MBL_LOCATION";
    public static final String PIN = "ru.mobnius.simple_core.data.configuration.PIN";
    public final static String PIN_CODE = "ru.mobnius.simple_core.data.configuration.PIN_CODE";
    public final static String TOUCH = "ru.mobnius.simple_core.data.configuration.TOUCH";

    @Nullable
    private static PreferencesManager preferencesManager;
    @Nullable
    protected SharedPreferences sharedPreferences;

    @Nullable
    public static PreferencesManager getInstance() {
        return preferencesManager;
    }

    public boolean isNotCreated() {
        return sharedPreferences == null;
    }

    public static void createInstance(final @NonNull Context context, final @NonNull String preferenceName) {
        preferencesManager = new PreferencesManager(context, preferenceName);
    }

    public PreferencesManager(final @NonNull Context context, final @NonNull String preferenceName) {
        try {
            final MasterKey mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(context,
                    preferenceName, mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        }
    }

    public boolean isDebug() {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(DEBUG, false);
    }

    public void setDebug(final boolean value) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putBoolean(DEBUG, value).apply();
    }

    public boolean getZip() {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(MBL_ZIP, false);
    }

    public boolean isPin() {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(PIN, false);
    }

    public void setPinAuth(final boolean value) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putBoolean(PIN, value).apply();
    }

    public void setPinCode(final @NonNull String pinCode) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putString(PIN_CODE, pinCode).apply();
    }

    @NonNull
    public String getPinCode() {
        if (sharedPreferences == null) {
            return StringUtil.EMPTY;
        }
        final String pinCode = sharedPreferences.getString(PIN_CODE, StringUtil.EMPTY);
        if (StringUtil.isEmpty(pinCode)) {
            return StringUtil.EMPTY;
        }
        return pinCode;
    }

    public boolean isTouch() {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean(TOUCH, false);
    }

    public void setTouchAuth(final boolean value) {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().putBoolean(TOUCH, value).apply();
    }


    @NonNull
    public String getLocation() {
        if (sharedPreferences == null) {
            return LocationManager.NETWORK_PROVIDER;
        }
        final String nonNullLocationSource = sharedPreferences.getString(MBL_LOCATION, LocationManager.NETWORK_PROVIDER);
        if (StringUtil.isEmpty(nonNullLocationSource)) {
            return LocationManager.NETWORK_PROVIDER;
        }
        return nonNullLocationSource;
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

    @VisibleForTesting
    public static void createTest(final @NonNull Context context) {
        preferencesManager = new PreferencesManager(context, "TEST");
    }
}
