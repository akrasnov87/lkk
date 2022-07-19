package ru.mobnius.cic.data.search;

/**
 * Интерфейс-контракт для реализации логики старых и новых фильтров
 */
public interface DifferenceHelper {
    boolean isDifferent();
    void setNewState();
    void returnToState();
}
