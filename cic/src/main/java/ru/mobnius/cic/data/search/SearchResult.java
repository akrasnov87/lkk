package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.RouteItem;

/**
 * Интерфейс представляющий результат глобального поиска или загаловка типа результата
 */
public interface SearchResult {
    int POINT_ITEM_PRIORITY = 1;

    int ALL_MODE = 0;
    int POINT_ONLY_MODE = 1;
    int ROUTE_ONLY_MODE = 2;

    int VIEW_TYPE_HEADER = 0;
    int VIEW_TYPE_ITEM = 1;

    int getViewType();

    @NonNull
    String getHeaderName();

    @NonNull
    String getId();

    @NonNull
    String getFirstLineText();

    @NonNull
    String getSecondLineText();

    @NonNull
    String getThirdLineText();

    @NonNull
    String getFourthLineText();

    int getPriority();

    @Nullable
    PointItem getPointItem();

    @Nullable
    RouteItem getRouteItem();
}
