package ru.mobnius.cic.ui.model.routestatus;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

public interface RouteStatus {

    String CURRENT_ROUTES_NAME = "Маршруты в работе";
    String FUTURE_ROUTES_NAME = "Маршруты в ожидании";
    String DONE_ROUTES_NAME = "Завершенные маршруты";
    String OVERDUE_ROUTES_NAME = "Просроченные маршруты";
    /**
     * Текущие маршруты
     */
    int CURRENT = 0;
    /**
     * Будущие маршруты
     */
    int FUTURE = 1;
    /**
     * Выполненые маршруты
     */
    int DONE = 2;
    /**
     * Просроченные маршруты
     */
    int OVERDUE = 3;

    int statusType();

    @NonNull
    String getName();

    @NonNull
    List<RouteItem> getRouteItems();

    boolean isNotEmpty();

    boolean isHintColor();
}
