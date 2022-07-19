package ru.mobnius.simple_core.data.packager;


import androidx.annotation.NonNull;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * информация о чтении мета информации
 */
public class MetaSize {
    /**
     * максимальная длина строки
     */
    static final int MAX_LENGTH = 16;

    /**
     * пакет создан
     */
    public static final int CREATED = 0;
    /**
     * пакет доставлен
     */
    public static final int DELIVERED = 1;
    /**
     * в обработке
     */
    public static final int PROCESSING = 2;
    /**
     * обработан
     */
    public static final int PROCESSED = 3;
    /**
     * Не доставлен
     */
    public static final int UN_DELIVERED = 8;
    /**
     * обработан с ошибкой
     */
    public static final int PROCESSED_ERROR = 9;

    /**
     * Длина метаинформации
     */
    public int metaSize;

    /**
     * Статус обработки пакета
     */
    public int status;

    /**
     * Тип пакета
     */
    @NonNull
    public final String type;

    public MetaSize(final int metaSize, final int status, final @NonNull String type) {
        this.metaSize = metaSize;
        this.status = status;
        this.type = type;
    }

    @NonNull
    public String toJsonString() {
        final StringBuilder result = new StringBuilder(type);
        result.append(metaSize);
        for (int i = 0; i < MAX_LENGTH - 1; i++) {
            if (i >= result.length()) {
                result.append(StringUtil.DOT);
            }
        }
        result.append(status);
        return result.toString();
    }
}
