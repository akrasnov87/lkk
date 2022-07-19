package ru.mobnius.cic.data.search;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.PointItem;

/**
 * Базовый класс для фильтрации точек маршрутов содержащий общую логику
 * по обработке старых/новых фильтров
 */
public abstract class BasePointFilter implements PointFilter, DifferenceHelper {
    protected boolean isAdded;
    protected boolean previousState;

    public BasePointFilter(final boolean isAdded) {
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

    @NonNull
    abstract String getSearchField(final @NonNull PointItem pointItem);
}
