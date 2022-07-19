package ru.mobnius.simple_core.data.packager;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Вложенный файл
 */
public class MetaAttachment {
    /**
     * размер вложения
     */
    @Expose
    public final int size;

    /**
     * имя файла или относительный путь
     */
    @Expose
    @NonNull
    public final String name;

    /**
     * ключ вложения
     */
    @Expose
    @NonNull
    public final String key;

    /**
     * конструктор
     *
     * @param size размер
     * @param name имя файла
     * @param key  ключ
     */
    public MetaAttachment(final int size, final @NonNull String name, final @NonNull String key) {
        this.size = size;
        this.key = key;
        this.name = name;
    }
}
