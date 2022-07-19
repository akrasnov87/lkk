package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

/**
 * Стандартный интерфейс обратного вызова для возврата результа
 * из асинхронных операций
 * @param <T> тип возвращаемого результата
 */
public interface OnCompleteCallback<T> {
    void onComplete(final @NonNull T result);
}
