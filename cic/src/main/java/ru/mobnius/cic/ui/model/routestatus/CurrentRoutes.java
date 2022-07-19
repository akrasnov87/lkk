package ru.mobnius.cic.ui.model.routestatus;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

public class CurrentRoutes implements RouteStatus {
    @NonNull
    private final List<RouteItem> currentRoutes;

    public CurrentRoutes(final @NonNull List<RouteItem> currentRoutes) {
        this.currentRoutes = currentRoutes;
    }

    @Override
    public int statusType() {
        return CURRENT;
    }

    @NonNull
    @Override
    public String getName() {
        return CURRENT_ROUTES_NAME;
    }

    @NonNull
    @Override
    public List<RouteItem> getRouteItems() {
        return currentRoutes;
    }

    @Override
    public boolean isNotEmpty() {
        return !currentRoutes.isEmpty();
    }

    @Override
    public boolean isHintColor() {
        return false;
    }
}
