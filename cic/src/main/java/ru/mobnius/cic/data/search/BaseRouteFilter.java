package ru.mobnius.cic.data.search;

/**
 * Базовый класс для фильтрации маршрутов содержащий общую логику
 * по обработке старых/новых фильтров
 */
public abstract class BaseRouteFilter implements RouteFilter, DifferenceHelper {
    protected boolean isAdded;
    protected boolean previousState;

    public BaseRouteFilter(final boolean isAdded) {
        this.isAdded = isAdded;
        this.previousState = isAdded;
    }

    public void reverseIsAdded() {
        this.isAdded = !this.isAdded;
    }

    public void addFilter() {
        this.isAdded = true;
    }

    public void removeFilter() {
        this.isAdded = false;
    }

    @Override
    public boolean isNotAdded() {
        return !isAdded;
    }

    @Override
    public boolean isAdded() {
        return isAdded;
    }

    @Override
    public boolean isDifferent() {
        return isAdded != previousState;
    }

    @Override
    public void setNewState() {
        previousState = isAdded;
    }

    @Override
    public void returnToState() {
        isAdded = previousState;
    }
}
