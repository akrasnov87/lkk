package ru.mobnius.cic.ui.model.routestatus;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

public class DoneRoutes implements RouteStatus {
    @NonNull
    private final List<RouteItem> doneRoutes;

    public DoneRoutes(final @NonNull List<RouteItem> doneRoutes) {
        this.doneRoutes = doneRoutes;
    }

    @Override
    public int statusType() {
        return DONE;
    }

    @NonNull
    @Override
    public String getName() {
        return DONE_ROUTES_NAME;
    }

    @NonNull
    @Override
    public List<RouteItem> getRouteItems() {
        return doneRoutes;
    }

    @Override
    public boolean isNotEmpty() {
        return !doneRoutes.isEmpty();
    }

    @Override
    public boolean isHintColor() {
        return true;
    }
}
