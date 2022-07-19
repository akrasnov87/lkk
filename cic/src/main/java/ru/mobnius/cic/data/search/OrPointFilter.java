package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс для ИЛИ фильтрации точек маршрутов по нескольким параметрам
 */
public class OrPointFilter implements PointFilter {
    @NonNull
    public final List<BasePointFilter> filterList;

    public OrPointFilter(final @NonNull List<BasePointFilter> criteriaList) {
        this.filterList = criteriaList;
    }

    @NonNull
    @Override
    public List<PointItem> satisfiesRequirements(@NonNull List<PointItem> rawItems, @NonNull String query) {
        if (filterList.size() == 0 || StringUtil.isEmpty(query)) {
            return rawItems;
        }
        return search(filterList, rawItems, query);
    }

    private List<PointItem> search(final List<BasePointFilter> tempFilterList, List<PointItem> rawItems, String query) {
        final List<PointItem> independend = new ArrayList<>();
        for (final PointItem pointItem : rawItems){
            boolean contains = false;
            for (final BasePointFilter filter : tempFilterList) {
                if (StringUtil.containsIgnoreCase(filter.getSearchField(pointItem), query)) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                independend.add(pointItem);
            }
        }
        return independend;
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

    public void clear() {
        filterList.clear();
    }

}
