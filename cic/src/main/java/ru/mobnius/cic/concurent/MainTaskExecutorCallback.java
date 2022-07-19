package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

/**
 * Обратный вызов для возврата результата выполнения в дополнительном потоке
 */
public interface MainTaskExecutorCallback<R> {
    void onCallableComplete(@NonNull final R result);

    void onCallableError(@NonNull final String error);
}