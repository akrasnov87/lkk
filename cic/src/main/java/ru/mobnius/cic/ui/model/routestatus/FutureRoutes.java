package ru.mobnius.cic.ui.model.routestatus;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

public class FutureRoutes implements RouteStatus {

    @NonNull
    private final List<RouteItem> futureRoutes;

    public FutureRoutes(final @NonNull List<RouteItem> futureRoutes) {
        this.futureRoutes = futureRoutes;
    }

    @Override
    public int statusType() {
        return FUTURE;
    }

    @NonNull
    @Override
    public String getName() {
        return FUTURE_ROUTES_NAME;
    }

    @NonNull
    @Override
    public List<RouteItem> getRouteItems() {
        return futureRoutes;
    }

    @Override
    public boolean isNotEmpty() {
        return !futureRoutes.isEmpty();
    }

    @Override
    public boolean isHintColor() {
        return true;
    }
}
