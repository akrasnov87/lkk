package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;

/**
 * Класс для И фильтрации точек маршрута по нескольким параметрам
 */
public class AndPointFilter implements PointFilter {
    @NonNull
    public final List<BasePointFilter> filterList;

    public AndPointFilter(final @NonNull List<BasePointFilter> criteriaList) {
        this.filterList = criteriaList;
    }

    @NonNull
    @Override
    public List<PointItem> satisfiesRequirements(@NonNull List<PointItem> rawItems, @NonNull String query) {
        if (filterList.size() == 0) {
            return rawItems;
        }
        final List<PointFilter> tempFilterList = new ArrayList<>(filterList);
        List<PointItem> resultList = tempFilterList.get(0).satisfiesRequirements(rawItems, query);
        tempFilterList.remove(0);
        for (final PointFilter criteria : tempFilterList) {
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

    public void clear() {
        filterList.clear();
    }
}
