package ru.mobnius.simple_core.data.packager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Блок с бинарными файлами
 */
public class BinaryBlock {
    @Nullable
    private ArrayList<FileBinary> items;

    /**
     * возвращается список файлов
     *
     * @return список файлов
     */
    @NonNull
    public FileBinary[] getFiles() {
        if (items == null || items.size() == 0)
            return new FileBinary[0];

        return items.toArray(new FileBinary[0]);
    }

    /**
     * добавление в блок бинарного файла
     *
     * @param name  имя файла
     * @param key   ключ файла. Должен быть уникальным
     * @param bytes масиив данных
     */
    public void add(final @NonNull String name, final @NonNull String key, final @NonNull byte[] bytes) {
        if (items == null)
            items = new ArrayList<>();

        items.add(new FileBinary(name, key, bytes));
    }

    /**
     * текущий блок превращается в массив байтов
     *
     * @return массив байтов
     */
    @NonNull
    public byte[] toBytes() {
        if (items == null || items.size() == 0)
            return new byte[0];

        int length = 0;
        for (int i = 0; i < items.size(); i++) {
            length += items.get(i).bytes.length;
        }

        final byte[] allByteArray = new byte[length];

        final ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        for (int i = 0; i < items.size(); i++) {
            buff.put(items.get(i).bytes);
        }

        return buff.array();
    }

    /**
     * информация для метаописания
     *
     * @return возвращается массив вложений
     */
    @NonNull
    public MetaAttachment[] getAttachments() {
        if (items == null || items.size() == 0)
            return new MetaAttachment[0];

        final MetaAttachment[] attachments = new MetaAttachment[items.size()];
        for (int i = 0; i < items.size(); i++) {
            final FileBinary item = items.get(i);
            attachments[i] = new MetaAttachment(item.bytes.length, item.name, item.key);
        }
        return attachments;
    }

    /**
     * очистка данных
     */
    public void clear() {
        if (items != null) {
            items.clear();
            items = null;
        }
    }
}
