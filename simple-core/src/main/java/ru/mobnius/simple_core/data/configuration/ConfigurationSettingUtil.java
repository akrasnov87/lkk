package ru.mobnius.simple_core.data.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.mobnius.simple_core.data.RequestManager;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.data.rpc.SingleItemQuery;
import ru.mobnius.simple_core.utils.StringUtil;

public class ConfigurationSettingUtil {
    public final static String ACTION = "setting";
    public final static String METHOD = "getSettings";
    private final static String KEY = "key";
    private final static String VALUE = "value";
    private final static String LABEL = "label";
    private final static String SUMMARY = "summary";
    private final static String TYPE = "type";
    private final static String MBL = "MBL";
    private final static String ZERO = "0";
    private final static String FALSE = "false";
    private final static String ONE = "1";
    private final static String TRUE = "true";

    /**
     * Преобразование значения настройки в Integer
     *
     * @param configurationSetting настройка
     * @return значение
     */
    public static int getIntegerValue(final @NonNull ConfigurationSetting configurationSetting, final int defaultValue) {
        if (configurationSetting.value != null && ConfigurationSetting.INTEGER.equals(configurationSetting.type)) {
            try {
                return Integer.parseInt(configurationSetting.value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Преобразование значения настройки в Double
     *
     * @param configurationSetting настройка
     * @return значение
     */
    public static double getDoubleValue(final @NonNull ConfigurationSetting configurationSetting, final double defaultValue) {
        if (configurationSetting.value != null && ConfigurationSetting.REAL.equals(configurationSetting.type)) {
            try {
                return Double.parseDouble(configurationSetting.value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Преобразование значения настройки в String
     *
     * @param configurationSetting настройка
     * @return значение
     */
    @NonNull
    public static String getStringValue(final @NonNull ConfigurationSetting configurationSetting) {
        if (configurationSetting.value != null && ConfigurationSetting.TEXT.equals(configurationSetting.type)) {
            return configurationSetting.value;
        }
        return StringUtil.EMPTY;
    }

    /**
     * Преобразование значения настройки в Boolean
     *
     * @param configurationSetting настройка
     * @return значение
     */
    public static boolean getBooleanValue(final @NonNull ConfigurationSetting configurationSetting, boolean defaultValue) {
        if (configurationSetting.value != null && ConfigurationSetting.BOOLEAN.equals(configurationSetting.type)) {
            switch (configurationSetting.value.toLowerCase()) {
                case ZERO:
                case FALSE:
                    return false;

                case ONE:
                case TRUE:
                    return true;
            }
        }
        return defaultValue;
    }

    /**
     * Чтение списка настроек из результата запроса
     *
     * @param result результат запроса
     * @return список настроек
     */
    @Nullable
    public static List<ConfigurationSetting> getConfigurationSettings(final @NonNull JsonObject result) {

        List<ConfigurationSetting> configurationSettings = new ArrayList<>();

        for (String key : result.keySet()) {
            final ConfigurationSetting configurationSetting = new ConfigurationSetting();

            final JsonObject jsonObject = result.getAsJsonObject(key);

            for (String name : jsonObject.keySet()) {
                String val = null;
                if (!jsonObject.get(name).isJsonNull()) {
                    val = jsonObject.get(name).getAsString();
                }
                switch (name) {
                    case KEY:
                        configurationSetting.key = val;
                        break;

                    case VALUE:
                        configurationSetting.value = val;
                        break;

                    case LABEL:
                        configurationSetting.label = val;
                        break;

                    case SUMMARY:
                        configurationSetting.summary = val;
                        break;

                    case TYPE:
                        configurationSetting.type = val;
                        break;
                }
            }

            configurationSettings.add(configurationSetting);
        }

        if (configurationSettings.size() > 0) {
            return configurationSettings;
        }
        return null;
    }

    /**
     * Получение настроек от сервера
     *
     * @return Возвращается список настроек
     */
    @Nullable
    public static List<ConfigurationSetting> getSettings(final @NonNull String baseUrl, final @NonNull BasicCredentials credentials) {
        String[] params = new String[1];
        params[0] = MBL;
        try {
            RPCResult[] results = RequestManager.rpc(baseUrl, credentials.getToken(), ACTION, METHOD, new SingleItemQuery(params));
            if (results != null && results.length > 0 && results[0].isSuccess()) {
                if (results[0].result == null || results[0].result.records == null || results[0].result.records[0] == null) {
                    return null;
                }
                return getConfigurationSettings(results[0].result.records[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
