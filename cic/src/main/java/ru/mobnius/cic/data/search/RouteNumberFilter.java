package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс фильтрации маршрута по наличию {@param query} в названии
 */
public class RouteNumberFilter extends BaseRouteFilter {

    public RouteNumberFilter(boolean isAdded) {
        super(isAdded);
    }

    @NonNull
    @Override
    public List<RouteItem> satisfiesRequirements(@NonNull List<RouteItem> rawItems, @NonNull String query) {
        if (StringUtil.isEmpty(query)) {
            return rawItems;
        }
        final List<RouteItem> quaired = new ArrayList<>();
        for (final RouteItem pointItem : rawItems) {
            if (StringUtil.containsIgnoreCase(pointItem.name, query)) {
                quaired.add(pointItem);
            }
        }
        return quaired;
    }

    @Override
    public boolean isAndFilter() {
        return false;
    }
}
