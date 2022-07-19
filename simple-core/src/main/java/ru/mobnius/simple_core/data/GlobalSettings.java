package ru.mobnius.simple_core.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Настройки
 * TODO: 04.06.2019 Существует временно потом нужно его менять
 */
public class GlobalSettings {
    /**
     * Применять ли статическую скорось передачи данных
     * По умолчанию использовать false. При тестирование удобно true
     */
    public static final boolean STATUS_TRANSFER_SPEED = false;


    public static final List<String> availableUrls = new ArrayList<>();
    public static final List<String> enviroments = new ArrayList<>();
    public static final ArrayList<Map<String, Object>> enviromentMap = new ArrayList<>();

    /**
     * Тут может быть три значения dev,test,release. По умолчанию release
     */
    public static String ENVIRONMENT = "/release";

    public static final String ENVIRONMENT_RELEASE = "/release";
    public static final String ENVIRONMENT_TEST = "/test";
    public static final String ENVIRONMENT_DEV = "/dev";
    public static final String ENVIRONMENT_DEMO = "/demo";

    public static String BASE_URL = "";

    /**
     * Адрес соединения с сервером
     *
     * @return адрес
     */
    @NonNull
    public static String getConnectUrl() {
        return BASE_URL + ENVIRONMENT;
    }

    /**
     * Адрес карт
     *
     * @return адрес
     */
    @NonNull
    public static String getMapUrl() {
        return BASE_URL + "/osm/";
    }
}
