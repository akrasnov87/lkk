package ru.mobnius.simple_core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import java.util.Date;

import ru.mobnius.simple_core.data.Version;

/**
 * Вспомогательная утилита для работы версией
 */
public class VersionUtil {
    public static final String VERSION_UNKNOWN = "0.0.0.0";

    /**
     * Возврщается версия приложения для пользователя (versionName)
     *
     * @param context activity
     * @return возвращается версия
     */
    @NonNull
    public static String getVersionName(final @NonNull Context context) {
        try {
            final PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName == null ? VERSION_UNKNOWN : pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return VERSION_UNKNOWN;
        }
    }

    /**
     * Возврщается укороченая версия приложения для пользователя (versionName)
     *
     * @param context activity
     * @return Возвращаются только первые два числа
     */
    @NonNull
    public static String getShortVersionName(final @NonNull Context context) {
        final String version = getVersionName(context);
        final String[] data = version.split("\\.");
        if (data.length > 2) {
            return data[0] + StringUtil.DOT + data[1];
        }
        return VERSION_UNKNOWN;
    }

    /**
     * Проверка на обновление версии
     *
     * @param context    контекст
     * @param newVersion новая версия на сервере
     * @return обновлять версию или нет
     */
    public static boolean isUpgradeVersion(final @NonNull Context context, final @NonNull String newVersion, final boolean isDebug) {
        final Version version = new Version();
        final String currentVersion = VersionUtil.getVersionName(context);
        final Date currentDate = version.getBuildDate(Version.BIRTH_DAY, currentVersion);
        final Date serverDate = version.getBuildDate(Version.BIRTH_DAY, newVersion);
        if (currentDate == null || serverDate == null) {
            return false;
        }
        return serverDate.getTime() > currentDate.getTime()
                && (version.getVersionState(newVersion) == Version.PRODUCTION || isDebug);
    }

}