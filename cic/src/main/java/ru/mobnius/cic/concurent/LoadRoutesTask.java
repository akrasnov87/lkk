package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.cic.ui.model.concurent.LoadRoutesResult;
import ru.mobnius.cic.ui.model.routestatus.CurrentRoutes;
import ru.mobnius.cic.ui.model.routestatus.DoneRoutes;
import ru.mobnius.cic.ui.model.routestatus.FutureRoutes;
import ru.mobnius.cic.ui.model.routestatus.OverdueRoutes;

/**
 * Класс первичного обработки новой фотографии
 * в дополнительном потоке
 */
public class LoadRoutesTask implements Callable<LoadRoutesResult> {

    @Override
    @NonNull
    public LoadRoutesResult call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new LoadRoutesResult();
        }
        final List<RouteItem> routes = DataManager.getInstance().getAllRouteItems();
        final List<RouteItem> currentRoutes = new ArrayList<>();
        final List<RouteItem> futureRoutes = new ArrayList<>();
        final List<RouteItem> doneRoutes = new ArrayList<>();
        final List<RouteItem> overdueRoutes = new ArrayList<>();

        for (RouteItem item : routes) {
            if (item.isRouteStatus(RouteItem.ROUTE_STATUS_DONE)) {
                doneRoutes.add(item);
                continue;
            }
            if (DataManager.getInstance().isCurrentRoute(item.id)) {
                currentRoutes.add(item);
                continue;
            }

            if (DataManager.getInstance().isOverdueRoute(item.id)) {
                overdueRoutes.add(item);
                continue;
            }
            futureRoutes.add(item);

        }
        if (currentRoutes.size() > 0) {
            sortList(currentRoutes);
        }
        if (futureRoutes.size() > 0) {
            sortList(futureRoutes);
        }
        if (doneRoutes.size() > 0) {
            sortList(doneRoutes);
        }
        if (overdueRoutes.size() > 0) {
            sortList(overdueRoutes);
        }

        return new LoadRoutesResult(new CurrentRoutes(currentRoutes),
                new FutureRoutes(futureRoutes),
                new DoneRoutes(doneRoutes),
                new OverdueRoutes(overdueRoutes));
    }

    @SuppressWarnings("ComparatorCombinators")
    private void sortList(final @NonNull List<RouteItem> list) {
        Collections.sort(list, (o1, o2) -> Integer.compare(o1.order, o2.order));
    }
}
