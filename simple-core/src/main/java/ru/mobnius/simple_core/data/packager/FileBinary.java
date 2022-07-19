package ru.mobnius.simple_core.data.packager;

import androidx.annotation.NonNull;

/**
 * Бинарный файл в пакете синхронизации
 */
public class FileBinary {
    /**
     * имя файла
     */
    @NonNull
    public final String name;
    /**
     * ключ файла
     */
    @NonNull
    public final String key;
    /**
     * Массив байтов
     */
    @NonNull
    public final byte[] bytes;

    public FileBinary(final @NonNull String name, final @NonNull String key, final @NonNull byte[] bytes) {
        this.name = name;
        this.key = key;
        this.bytes = bytes;
    }
}
