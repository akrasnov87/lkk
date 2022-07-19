package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class SortItem {
    public final static String ASC = "ASC";
    public final static String DESC = "DESC";

    /**
     * сортировка
     *
     * @param property  свойство
     * @param direction тип сортировки ASC, DESC
     */
    public SortItem(final @NonNull String property, final @NonNull String direction) {
        this.direction = direction;
        this.property = property;
    }

    /**
     * Сортировка по убыванию
     *
     * @param property свойтсва для сортировки
     */
    public SortItem(final @NonNull String property) {
        this(property, DESC);
    }

    @NonNull
    @Expose
    public final String property;
    @NonNull
    @Expose
    public final String direction;
}
