package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.search.AndPointFilter;
import ru.mobnius.cic.data.search.OrPointFilter;
import ru.mobnius.cic.ui.model.PointItem;

/**
 * Класс выполняющий поиск и фильтрацию точек маршрута
 * в дополнительном потоке
 */
public class RefreshPointTask implements Callable<List<PointItem>> {
    @NonNull
    private List<PointItem> list;
    @Nullable
    private final Comparator<PointItem> comparator;
    @NonNull
    private final OrPointFilter orPointFilter;
    @NonNull
    private final AndPointFilter andPointFilter;
    @NonNull
    private final String query;

    public RefreshPointTask(final @NonNull String query,
                            final @NonNull List<PointItem> list,
                            final @NonNull OrPointFilter orPointFilter,
                            final @NonNull AndPointFilter andPointFilter,
                            final @Nullable Comparator<PointItem> comparator) {
        this.list = list;
        this.comparator = comparator;
        this.andPointFilter = andPointFilter;
        this.orPointFilter = orPointFilter;
        this.query = query;
    }

    @Override
    public List<PointItem> call() throws Exception {
        list = orPointFilter.satisfiesRequirements(list, query);
        list = andPointFilter.satisfiesRequirements(list, query);
        if (comparator != null) {
            Collections.sort(list, comparator);
        }
        return list;
    }
}
