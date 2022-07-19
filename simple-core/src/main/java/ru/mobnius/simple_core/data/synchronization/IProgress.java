package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.data.synchronization.utils.transfer.ITransferStatusCallback;

/**
 * Интерфейс для отслеживания процесса синхронизации
 */
public interface IProgress extends ITransferStatusCallback {

    /**
     * Отмена синхронизации
     *
     * @param synchronization объект синхронизации
     */
    void onStop(@NonNull OnSynchronizationListeners synchronization);

    /**
     * Прогресс выполнения синхрониации
     *
     * @param synchronization объект синхронизации
     * @param step            шаг выполнения
     * @param message         текстовое сообщения
     * @param tid             Идентификатор транзакции
     */
    void onProgress(@NonNull OnSynchronizationListeners synchronization, int step, @NonNull String message, @Nullable String tid);

    /**
     * обработчсик ошибок
     *
     * @param synchronization объект синхронизации
     * @param step            шаг выполнения
     * @param message         текстовое сообщения
     * @param tid             Идентификатор транзакции
     */
    void onError(@NonNull OnSynchronizationListeners synchronization, int step, @NonNull String message, @Nullable String tid);
}
