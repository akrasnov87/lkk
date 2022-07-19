package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

/**
 * Интерфейс-контракт для реализации логики использования фильтров маршрутов
 */
public interface RouteFilter {
    int AREA_ADDRESS = 0;
    int AREA_SUBSCR_NUMBER = 1;
    int AREA_DEVICE_NUMBER = 2;
    int TYPE_ALL = 3;
    int TYPE_PERSON = 4;
    int TYPE_COMPANY = 5;
    int STATUS_ALL = 6;
    int STATUS_DONE = 7;
    int STATUS_UNDONE = 8;

    @NonNull
    List<RouteItem> satisfiesRequirements(final @NonNull List<RouteItem> rawItems, final @NonNull String query);

    boolean isAndFilter();

    boolean isAdded();

    boolean isNotAdded();
}
