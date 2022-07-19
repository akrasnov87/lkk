package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Базовый класс представления таблицы для синхронизации
 */
public class Entity {

    @NonNull
    public String nameEntity;

    /**
     * Идентификатор сущности. Предназначен для работы с пакетами
     */
    @NonNull
    public String tid;

    /**
     * имя таблицы
     */
    @NonNull
    public final String tableName;

    /**
     * список колонок для выборки
     */
    @NonNull
    public final String select = StringUtil.EMPTY;

    /**
     * Передача данных на сервер
     */
    public final boolean to;

    /**
     * Получение данных от сервера
     */
    public final boolean from;
    /**
     * является справочником
     */
    protected boolean isDictionary = false;

    /**
     * Обработка завершена или нет
     */
    public boolean finished = false;

    /**
     * После завершения требуется очистка
     */
    public final boolean clearable = false;

    /**
     * Принудительная передача данных в режиме many
     */
    public final boolean many = false;

    /**
     * использовать функцию cf_ для получения данных
     */
    public boolean useCFunction;

    /**
     * параметры в функцию
     */
    @Nullable
    public Object[] params;

    /**
     * фильтрация
     */
    @Nullable
    public Object[] filters;

    /**
     * Конструктор. По умолчанию указывается что разрешена отправка данных на сервер to = true
     *
     * @param tableName имя таблицы
     */
    public Entity(final @NonNull String tableName) {
        this(tableName, true, false);
    }

    /**
     * Конструктор
     *
     * @param tableName имя таблицы
     * @param to        разрешена отправка данных на сервер
     * @param from      разрешена возможность получения данных с сервера
     */
    public Entity(final @NonNull String tableName, final boolean to, final boolean from) {
        this.tableName = tableName;
        this.to = to;
        this.from = from;
        this.tid = UUID.randomUUID().toString();
        nameEntity = BaseApp.COMMON;
    }

    /**
     * Создание сущности
     *
     * @param tableName имя таблицы
     * @param to        разрешена отправка данных на сервер
     * @param from      разрешена возможность получения информации с сервера
     * @return Возвращается сущность
     */
    public static Entity createInstance(final @NonNull String tableName, final boolean to, final boolean from) {
        return new Entity(tableName, to, from);
    }

    /**
     * Уставнваливается идентификатор для сущности
     *
     * @param tid идентификатор
     * @return возвращается текущая сущность
     */
    public Entity setTid(final @NonNull String tid) {
        this.tid = tid;
        return this;
    }


    /**
     * обработка сущности завершена
     */
    public void setFinished() {
        this.finished = true;
    }

    /**
     * установить параметр useCFunction
     *
     * @return текущий объект
     */
    public Entity setUseCFunction() {
        this.useCFunction = true;
        return this;
    }

    /**
     * параметры в RPC запрос
     *
     * @param params параметры
     * @return текущий объект
     */
    public Entity setParam(final @NonNull Object... params) {
        this.params = params;
        return this;
    }

}
