package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.search.AndPointFilter;
import ru.mobnius.cic.data.search.OrPointFilter;
import ru.mobnius.cic.ui.model.PointItem;

/**
 * Класс первичной загрузки точек маршрута
 * в дополнительном потоке
 */
public class LoadPointsTask implements Callable<List<PointItem>> {
    @NonNull
    private final String routeId;
    @NonNull
    private final String query;
    @NonNull
    private final OrPointFilter orPointFilter;
    @NonNull
    private final AndPointFilter andPointFilter;

    public LoadPointsTask(final @NonNull String routeId,
                          final @NonNull String query,
                          final @NonNull OrPointFilter orPointFilter,
                          final @NonNull AndPointFilter andPointFilter) {
        this.routeId = routeId;
        this.query = query;
        this.orPointFilter = orPointFilter;
        this.andPointFilter = andPointFilter;
    }

    @NonNull
    @Override
    public List<PointItem> call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new ArrayList<>();
        }
        List<PointItem> result = DataManager.getInstance().getRoutePointItems(routeId);
        result = orPointFilter.satisfiesRequirements(result, query);
        result = andPointFilter.satisfiesRequirements(result, query);
        return result;
    }
}
