package ru.mobnius.simple_core.data.configuration;


import androidx.annotation.Nullable;

public class ConfigurationSetting {

    public final static String INTEGER = "INTEGER";
    public final static String REAL = "REAL";
    public final static String TEXT = "TEXT";
    public final static String BOOLEAN = "BOOLEAN";
    public final static String DATE = "DATE";

    /**
     * Ключ
     */
    @Nullable
    public String key;
    /**
     * Значение
     */
    @Nullable
    public String value;
    @Nullable
    public String label;
    /**
     * Описание
     */
    @Nullable
    public String summary;
    /**
     * Тип значения в настройке.
     * INTEGER, REAL, TEXT, BLOB
     */
    @Nullable
    public String type;

    public ConfigurationSetting() {
    }

    public ConfigurationSetting(final @Nullable String key,
                                final @Nullable String value,
                                final @Nullable String type) {
        this();
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public ConfigurationSetting(final @Nullable String key,
                                final @Nullable String value,
                                final @Nullable String label,
                                final @Nullable String summary,
                                final @Nullable String type) {
        this(key, value, type);
        this.label = label;
        this.summary = summary;
    }

}
