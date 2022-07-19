package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

/**
 * Класс для И фильтрации маршрутов по нескольким параметрам
 */
public class AndRouteFilter implements RouteFilter {
    @NonNull
    public final List<BaseRouteFilter> filterList;

    public AndRouteFilter(final @NonNull List<BaseRouteFilter> criteriaList) {
        this.filterList = criteriaList;
    }

    @NonNull
    @Override
    public List<RouteItem> satisfiesRequirements(@NonNull List<RouteItem> rawItems, @NonNull String query) {
        if (filterList.size() == 0) {
            return rawItems;
        }
        final List<RouteFilter> tempFilterList = new ArrayList<>(filterList);
        List<RouteItem> resultList = tempFilterList.get(0).satisfiesRequirements(rawItems, query);
        tempFilterList.remove(0);
        for (final RouteFilter criteria : tempFilterList) {
            resultList = criteria.satisfiesRequirements(resultList, query);
        }
        return resultList;
    }

    @Override
    public boolean isAndFilter() {
        return true;
    }

    @Override
    public boolean isAdded() {
        return false;
    }

    @Override
    public boolean isNotAdded() {
        return false;
    }
}
