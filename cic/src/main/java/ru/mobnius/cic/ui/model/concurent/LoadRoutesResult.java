package ru.mobnius.cic.ui.model.concurent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.cic.ui.model.routestatus.CurrentRoutes;
import ru.mobnius.cic.ui.model.routestatus.DoneRoutes;
import ru.mobnius.cic.ui.model.routestatus.FutureRoutes;
import ru.mobnius.cic.ui.model.routestatus.OverdueRoutes;

public class LoadRoutesResult {
    @NonNull
    public final CurrentRoutes currentRoutes;
    @NonNull
    public final FutureRoutes futureRoutes;
    @NonNull
    public final DoneRoutes doneRoutes;
    @NonNull
    public final OverdueRoutes overdueRoutes;

    public LoadRoutesResult(final @NonNull CurrentRoutes currentRoutes,
                            final @NonNull FutureRoutes futureRoutes,
                            final @NonNull DoneRoutes doneRoutes,
                            final @NonNull OverdueRoutes overdueRoutes) {

        this.currentRoutes = currentRoutes;
        this.futureRoutes = futureRoutes;
        this.doneRoutes = doneRoutes;
        this.overdueRoutes = overdueRoutes;
    }

    public LoadRoutesResult() {

        this.currentRoutes = new CurrentRoutes(new ArrayList<>());
        this.futureRoutes = new FutureRoutes(new ArrayList<>());
        this.doneRoutes = new DoneRoutes(new ArrayList<>());
        this.overdueRoutes = new OverdueRoutes(new ArrayList<>());
    }

    public boolean isEmpty() {
        return currentRoutes.getRouteItems().isEmpty() &&
                futureRoutes.getRouteItems().isEmpty() &&
                doneRoutes.getRouteItems().isEmpty() &&
                overdueRoutes.getRouteItems().isEmpty();
    }

    @NonNull
    public List<RouteItem> getAllRouteItems() {
        final List<RouteItem> routeItems = new ArrayList<>();
        routeItems.addAll(currentRoutes.getRouteItems());
        routeItems.addAll(futureRoutes.getRouteItems());
        routeItems.addAll(doneRoutes.getRouteItems());
        routeItems.addAll(overdueRoutes.getRouteItems());
        return routeItems;
    }
}
