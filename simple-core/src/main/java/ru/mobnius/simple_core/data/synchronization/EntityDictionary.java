package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.BaseApp;

/**
 * Представление справочника для синхронизации
 */
public class EntityDictionary extends Entity {

    /**
     * Конструктор
     *
     * @param tableName имя таблицы
     * @param to        разрешена отправка данных на сервер
     * @param from      разрешена возможность получения данных с сервера
     */
    public EntityDictionary(final @NonNull String tableName, final boolean to, final boolean from) {
        super(tableName, to, from);
        nameEntity = BaseApp.DICTIONARY;
        isDictionary = true;
    }
}
