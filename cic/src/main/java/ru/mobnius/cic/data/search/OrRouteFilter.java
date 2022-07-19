package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

/**
 * Класс для ИЛИ фильтрации маршрутов по нескольким параметрам
 */
public class OrRouteFilter implements RouteFilter {
    @NonNull
    public final List<BaseRouteFilter> filterList;

    public OrRouteFilter(final @NonNull List<BaseRouteFilter> criteriaList) {
        this.filterList = criteriaList;
    }

    @NonNull
    @Override
    public List<RouteItem> satisfiesRequirements(@NonNull List<RouteItem> rawItems, @NonNull String query) {
        if (filterList.size() == 0) {
            return rawItems;
        }
        final List<RouteFilter> tempFilterList = new ArrayList<>(filterList);
        final List<RouteItem> resultList = new ArrayList<>();
        for (final RouteFilter criteria : tempFilterList) {
            if (criteria.isNotAdded()) {
                continue;
            }
            final List<RouteItem> nextList = criteria.satisfiesRequirements(rawItems, query);
            for (final RouteItem item : nextList) {
                if (!resultList.contains(item)) {
                    resultList.add(item);
                }
            }
        }
        return resultList;
    }

    @Override
    public boolean isAndFilter() {
        return false;
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
