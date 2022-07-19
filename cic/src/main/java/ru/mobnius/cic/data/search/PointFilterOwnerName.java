package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.simple_core.utils.StringUtil;

public class PointFilterOwnerName extends BasePointFilter {

    public PointFilterOwnerName(boolean isAdded) {
        super(isAdded);
    }

    @NonNull
    @Override
    String getSearchField(@NonNull PointItem pointItem) {
        return pointItem.owner;
    }

    @NonNull
    @Override
    public List<PointItem> satisfiesRequirements(@NonNull List<PointItem> rawItems, @NonNull String query) {
        if (StringUtil.isEmpty(query)) {
            return rawItems;
        }
        List<PointItem> independend = new ArrayList<>(rawItems);
        final Iterator<PointItem> iterator = independend.iterator();
        while (iterator.hasNext()) {
            if (StringUtil.containsNotIgnoreCase(iterator.next().owner, query)) {
                iterator.remove();
            }
        }
        return independend;
    }

    @Override
    public boolean isAndFilter() {
        return false;
    }

}

