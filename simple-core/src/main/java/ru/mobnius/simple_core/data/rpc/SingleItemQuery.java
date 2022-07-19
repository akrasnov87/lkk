package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import ru.mobnius.simple_core.data.synchronization.BaseSynchronization;

/**
 * Параметры передаваемые в одиночных запросах RPC
 */
public class SingleItemQuery {

    public SingleItemQuery(final @Nullable Object... obj) {
        this.params = obj;
        this.limit = BaseSynchronization.MAX_COUNT_IN_QUERY;
    }

    public SingleItemQuery(final @Nullable String... obj) {
        this.limit = BaseSynchronization.MAX_COUNT_IN_QUERY;
        this.params = obj;
    }

    public void setFilter(Object[] items) {
        filter = items;
    }

    @Expose
    private Object[] filter;

    /**
     * дополнительные параметры. Применяется для вызова одиночных метод
     */
    @Nullable
    @Expose
    private final Object[] params;

    @Expose
    public final int limit;

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
