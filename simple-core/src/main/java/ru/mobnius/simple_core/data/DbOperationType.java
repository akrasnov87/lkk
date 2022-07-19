package ru.mobnius.simple_core.data;

/**
 * Тип операций совершаемых с объектами БД
 */
public interface DbOperationType {
    /**
     * Создан
     */
    String CREATED = "CREATED";

    /**
     * Обновлен
     */
    String UPDATED = "UPDATED";

    /**
     * Удален
     */
    String REMOVED = "REMOVED";
}
