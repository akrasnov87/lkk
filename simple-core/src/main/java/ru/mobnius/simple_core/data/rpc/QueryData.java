package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

import ru.mobnius.simple_core.utils.StringUtil;

public class QueryData {

    public QueryData(final @NonNull String select, final @Nullable String alias, final @Nullable Object[] filter, final int limit) {
        this.select = select;
        this.alias = alias;
        this.query = StringUtil.EMPTY;
        this.filter = filter;
        this.limit = limit;
    }

    @NonNull
    @Expose
    public final String select;

    @NonNull
    @Expose
    public final String query;

    /**
     * Псевдоним для переопределения результата, может буть null
     */
    @Nullable
    @Expose
    public final String alias;

    /**
     * Фильтрация по правилам RPC
     * Элементом массива может являтся строка FILTER_*, либо FilterItem
     */
    @Nullable
    @Expose
    public final Object[] filter;
    /**
     * Сортировка
     */
    @Nullable
    @Expose
    public SortItem[] sort;

    @Expose
    public final int page = 1;
    @Expose
    public final int start = 0;
    @Expose
    public final int limit;


}
