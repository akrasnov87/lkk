package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.BaseApp;

/**
 * Представление вложения(файл изображения) для синхронизации
 */
public class EntityAttachment extends Entity {

    /**
     * Конструктор
     *
     * @param tableName имя таблицы
     * @param to        разрешена отправка данных на сервер
     * @param from      разрешена возможность получения данных с сервера
     */
    public EntityAttachment(final @NonNull String tableName, final boolean to, final boolean from) {
        super(tableName, to, from);
        nameEntity = BaseApp.ATTACHMENTS;
    }
}
