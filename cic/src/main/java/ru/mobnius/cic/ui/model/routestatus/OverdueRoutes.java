package ru.mobnius.cic.ui.model.routestatus;

import androidx.annotation.NonNull;

import java.util.List;

import ru.mobnius.cic.ui.model.RouteItem;

public class OverdueRoutes implements RouteStatus {
    @NonNull
    private final List<RouteItem> overdueRoutes;

    public OverdueRoutes(final @NonNull List<RouteItem> overdueRoutes) {
        this.overdueRoutes = overdueRoutes;
    }

    @Override
    public int statusType() {
        return OVERDUE;
    }

    @NonNull
    @Override
    public String getName() {
        return OVERDUE_ROUTES_NAME;
    }

    @NonNull
    @Override
    public List<RouteItem> getRouteItems() {
        return overdueRoutes;
    }

    @Override
    public boolean isNotEmpty() {
        return !overdueRoutes.isEmpty();
    }

    @Override
    public boolean isHintColor() {
        return true;
    }
}
