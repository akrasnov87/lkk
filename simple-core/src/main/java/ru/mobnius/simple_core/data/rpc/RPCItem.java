package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import ru.mobnius.simple_core.utils.DateUtil;

/**
 * Запрос для RPC
 */
public class RPCItem {

    public final static String QUERY = "Query";
    public final static String SELECT = "Select";
    public final static String ADD = "Add";
    public final static String ADD_OR_UPDATE = "AddOrUpdate";

    /**
     * код запроса. в рамка группы должен быть уникален
     */
    @Expose
    public final int tid;

    @NonNull
    @Expose
    public final String type = "rpc";

    /**
     * Схема
     */
    @NonNull
    @Expose
    public String schema = "dbo";

    /**
     * Действие
     */
    @NonNull
    @Expose
    public final String action;
    /**
     * Метод
     */
    @NonNull
    @Expose
    public final String method;
    /**
     * данные для передачи
     */
    @NonNull
    @Expose
    public final Object[] data;

    /**
     * тип, всегда rpc
     */

    public RPCItem(final @NonNull String action, final @NonNull String method, final @NonNull Object[] data) {
        this.tid = DateUtil.geenerateTid();
        this.action = action;
        this.method = method;
        this.data = data;
    }


    @NonNull
    public String toJsonString() {
        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    /**
     * Объект для добавления записей
     *
     * @param action сущность
     * @param items  список объектов
     * @return объект
     */
    @NonNull
    public static RPCItem addItems(final @NonNull String action, final @NonNull Object[] items) {
        return new RPCItem(action, ADD, items);
    }

    /**
     * Объект для добавления записи
     *
     * @param action сущность
     * @param item   объект
     * @return объект
     */
    public static RPCItem addItem(final @NonNull String action, final @Nullable Object item) {
        final Object[] array = new Object[1];
        array[0] = item;
        return new RPCItem(action, ADD_OR_UPDATE, array);
    }

}
