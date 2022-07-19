package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ITransferStatusCallback {
    /**
     * обработчик запуска операции
     *
     * @param tid      идентификатор транзакции
     * @param transfer обработчик передачи данных
     */
    void onStartTransfer(@NonNull String tid, @NonNull Transfer transfer);

    /**
     * обработчик перезапуска операции
     *
     * @param tid      идентификатор транзакции
     * @param transfer обработчик передачи данных
     */
    void onRestartTransfer(@NonNull String tid, @NonNull Transfer transfer);

    /**
     * обработчик выполнения задания
     *
     * @param progress прогресс
     */
    void onPercentTransfer(@NonNull String tid, @NonNull TransferProgress progress, @NonNull Transfer transfer);

    /**
     * обработчик остановки операции
     *
     * @param tid      идентификатор транзакции
     * @param transfer обработчик передачи данных
     */
    void onStopTransfer(@NonNull String tid, @NonNull Transfer transfer);

    /**
     * обработчик завершения операции
     *
     * @param tid      идентификатор транзакции
     * @param transfer обработчик передачи данных
     * @param data     дополнительные данные
     */
    void onEndTransfer(@NonNull String tid, @NonNull Transfer transfer, @Nullable Object data);

    /**
     * обработчик возникновения ошибки
     *
     * @param tid      идентификатор транзакции
     * @param message  текст сообщения
     * @param transfer обработчик передачи данных
     */
    void onErrorTransfer(@NonNull String tid, @NonNull Transfer transfer, @NonNull String message);
}
