package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;

/**
 * Интерфейс-контракт для реализации логики использования фильтров точек маршрутов
 */
public interface PointFilter {
    int AREA_ALL = 0;
    int AREA_ADDRESS = 1;
    int AREA_SUBSCR_NUMBER = 2;
    int AREA_DEVICE_NUMBER = 3;
    int AREA_OWNER_NAME = 4;
    int TYPE_ALL = 5;
    int TYPE_PERSON = 6;
    int TYPE_COMPANY = 7;
    int STATUS_ALL = 8;
    int STATUS_DONE = 9;
    int STATUS_UNDONE = 10;

    @NonNull
    List<PointItem> satisfiesRequirements(final @NonNull List<PointItem> rawItems, final @NonNull String query);

    boolean isAndFilter();

    boolean isAdded();

    boolean isNotAdded();

}
