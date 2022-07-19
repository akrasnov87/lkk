package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

/**
 * Стандартный интерфейс обратного вызова для возврата результа
 * из асинхронных операций в случае возникновения ошибки
 * @param <T> тип возвращаемого результата при возникновении ошибки
 */
public interface OnErrorCallback<T>{
    void onError(final @NonNull T error);
}
