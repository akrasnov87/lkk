package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.Date;

import io.socket.emitter.Emitter;
import ru.mobnius.simple_core.data.synchronization.OnSynchronizationListeners;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Слушатель для механизма передачи и получения данных
 */
public class TransferListener implements Emitter.Listener {

    public static final int START = 0;
    public static final int RESTART = 1;
    public static final int PERCENT = 2;
    public static final int STOP = 3;
    public static final int END = 4;
    public static final int ERROR = 5;
    @NonNull
    private final String tid;
    @NonNull
    private final ITransferStatusCallback statusCallback;
    @NonNull
    protected final Transfer transfer;
    @NonNull
    private final OnSynchronizationListeners synchronization;
    @Nullable
    protected Date iterationStartTime;

    /**
     * конструктор
     *
     * @param tid            идентификатор транзакции
     * @param statusCallback статус
     */
    public TransferListener(final @NonNull OnSynchronizationListeners synchronization,
                            final @NonNull String tid,
                            final @NonNull Transfer transfer,
                            final @NonNull ITransferStatusCallback statusCallback) {
        this.tid = tid;
        this.statusCallback = statusCallback;
        this.transfer = transfer;
        this.synchronization = synchronization;
    }

    /**
     * Время начала итерации
     *
     * @return время
     */
    @NonNull
    public Date getIterationStartTime() {
        return iterationStartTime == null ? new Date() : iterationStartTime;
    }

    public void onStart() {
        onHandler(START, tid, transfer, null);
    }

    public void onRestart() {
        onHandler(RESTART, tid, transfer, null);
    }

    public void onPercent(final double percent, final @NonNull TransferSpeed speed, final long lastTime, final @NonNull TransferData transferData) {
        iterationStartTime = new Date();
        final TransferProgress progress = TransferProgress.getInstance(percent, speed, transferData, lastTime);

        onHandler(PERCENT, tid, transfer, progress);
    }

    public void onStop() {
        onHandler(STOP, tid, transfer, null);
    }

    public void onEnd(final @NonNull byte[] bytes) {
        onPercent(100,
                TransferSpeed.getInstance(transfer.getChunk(), new Date().getTime() - getIterationStartTime().getTime()),
                0,
                TransferData.getInstance(bytes.length, bytes.length));
        onHandler(END, tid, transfer, bytes);
    }

    /**
     * обработчик ошибок
     *
     * @param message текст сообщения
     */
    public void onError(final @NonNull String message) {
        onHandler(ERROR, tid, transfer, message);
    }

    @Override
    public void call(Object... args) {

    }

    private void onCallHandler(final int type, final @NonNull String tid, final @NonNull Transfer transfer, final @Nullable Object data) {
        if (statusCallback == null) {
            return;
        }
        if (synchronization.getEntities(tid).length == 0) {
            return;
        }
        switch (type) {
            case START:
                statusCallback.onStartTransfer(tid, transfer);
                break;

            case RESTART:
                statusCallback.onRestartTransfer(tid, transfer);
                break;

            case PERCENT:
                TransferProgress progress = TransferProgress.getEmptyInstance();
                if (data instanceof TransferProgress) {
                    progress = (TransferProgress) data;
                }
                statusCallback.onPercentTransfer(tid, progress, transfer);
                break;

            case STOP:
                statusCallback.onStopTransfer(tid, transfer);
                break;

            case END:
                statusCallback.onEndTransfer(tid, transfer, data);
                break;

            case ERROR:
                String error = StringUtil.EMPTY;
                if (data instanceof String) {
                    error = (String) data;
                }
                statusCallback.onErrorTransfer(tid, transfer, error);
                break;
        }
    }

    private void onHandler(final int type,
                           final @NonNull String tid,
                           final @NonNull Transfer transfer,
                           final @Nullable Object data) {
        onCallHandler(type, tid, transfer, data);
    }

    /**
     * вычисление оставшегося времени
     *
     * @param dtStart дата начала процесса
     * @param percent процент выполнения
     * @return время в милисекундах
     */
    protected long getLastTime(Date dtStart, int percent) {
        if (percent == 0)
            percent = 1;
        // прошло время с начала запуска
        long workTime = new Date().getTime() - dtStart.getTime();
        // приблизительная продолжительность
        long totalTime = (workTime * 100) / percent;

        return totalTime - workTime;
    }
}
