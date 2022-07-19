package ru.mobnius.simple_core.data.synchronization.meta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.data.rpc.QueryData;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.rpc.SingleItemQuery;

public class TableQuery {
    /**
     * имя таблицы для обработки
     */
    @NonNull
    public final String action;

    /**
     * псевдоним action
     */
    @Nullable
    public final String alias;

    /**
     * список полей которые требуется получить от сервера
     */
    @NonNull
    public final String select;


    /**
     * @param alias псевдоним
     */
    public TableQuery(final @NonNull String action, final @Nullable String alias, final @NonNull String select) {
        this.action = action;
        this.alias = alias;
        this.select = select;
    }

    /**
     * Создание объекта
     *
     * @param action таблицы
     * @param select выборка полей
     * @return объект TableQuery
     */
    public TableQuery(final @NonNull String action, final @NonNull String select) {
        this(action, null, select);
    }

    /**
     * Преобразование в RPC запрос
     *
     * @param limit   лимит
     * @param filters фильтрация
     * @return RPC запрос
     */
    @NonNull
    public RPCItem toRPCQuery(final int limit, final @Nullable Object[] filters) {
        final QueryData query = new QueryData(select, alias, filters, limit);
        final Object[] arr = new Object[1];
        arr[0] = query;
        return new RPCItem(action, RPCItem.QUERY, arr);
    }

    /**
     * Преобразование в RPC запрос
     *
     * @param obj дополнительные параметры в функцию
     * @return RPC запрос
     */
    @NonNull
    public RPCItem toRPCSelect(final @Nullable Object... obj) {
        final Object [] arr = new Object[1];
        arr[0] = new SingleItemQuery(obj);
        return new RPCItem(action, RPCItem.SELECT, arr);
    }
}
