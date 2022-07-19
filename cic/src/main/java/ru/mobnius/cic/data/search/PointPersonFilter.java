package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс фильтрации точки по тому является ли абонент ФЛ
 */
public class PointPersonFilter extends BasePointFilter {
    public PointPersonFilter(boolean isAdded) {
        super(isAdded);
    }

    @NonNull
    @Override
    String getSearchField(final @NonNull PointItem pointItem) {
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public List<PointItem> satisfiesRequirements(@NonNull List<PointItem> rawItems, @NonNull String query) {
        if (isNotAdded()) {
            return rawItems;
        }
        final Iterator<PointItem> iterator = rawItems.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().person) {
                iterator.remove();
            }
        }
        return rawItems;
    }

    @Override
    public boolean isAndFilter() {
        return true;
    }
}
