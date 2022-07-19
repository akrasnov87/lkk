package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * История изменения статуса
 */
public class RouteInfoHistory {
    /**
     * Наименование статуса
     */
    @NonNull
    public final String status;
    /**
     * Дата создания статуса
     */
    @NonNull
    public final Date date;

    public RouteInfoHistory(final @NonNull String status,
                            final @NonNull Date date){
        this.status = status;
        this.date = date;
    }
}
