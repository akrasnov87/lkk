package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import static ru.mobnius.simple_core.data.GlobalSettings.STATUS_TRANSFER_SPEED;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.socket.client.Socket;
import ru.mobnius.simple_core.data.synchronization.OnSynchronizationListeners;
import ru.mobnius.simple_core.utils.StringUtil;

public abstract class Transfer {

    protected final String UPLOAD_TAG = "UPLOAD_TRANSFER";
    protected final String DOWNLOAD_TAG = "DOWNLOAD_TRANSFER";

    protected final String EVENT_UPLOAD = "upload";
    protected final String EVENT_DOWNLOAD = "download";

    /**
     * размер передоваемых данных
     */
    // TODO: должно быть вычисляемым
    private final int CHUNK = 1024;

    /**
     * вычисленный размер блоков для передачи за секунду
     */
    private long calcChunk = CHUNK;

    /**
     * интервал в течении которого происходят вычисления
     */
    public static int INTERVAL = 1000;

    /**
     * Подключение через websocket
     */
    @NonNull
    protected final Socket socket;

    /**
     * версия протокола синхронизации
     */
    @NonNull
    protected final String protocolVersion;

    /**
     * слушатель "регистрации" пользователя на сервере
     */
    @Nullable
    private TransferRegistryListener transferRegistryListener;

    /**
     * слушатель потери соединения с сервером
     */
    @Nullable
    private TransferDisconnectListener transferDisconnectListener;

    /**
     * обработчик обратного вызова
     */
    @NonNull
    protected ITransferStatusCallback callback;

    /**
     * идентификатор транзакции
     */
    @NonNull
    protected final String tid;

    @NonNull
    protected final OnSynchronizationListeners synchronization;

    /**
     * конструктор
     *
     * @param synchronization текущая синхронизация в рамках которой выполняется процесс
     * @param socket          сокет соединение
     * @param version         версия синхронизации
     * @param tid             идентификатор транзакции
     */
    public Transfer(final @NonNull OnSynchronizationListeners synchronization,
                    final @NonNull ITransferStatusCallback callback,
                    final @NonNull Socket socket,
                    final @NonNull String version,
                    final @NonNull String tid) {
        this.callback = callback;
        this.socket = socket;
        this.protocolVersion = version;
        this.synchronization = synchronization;
        this.tid = tid;
    }

    /**
     * Настройка слушителя отсуствия соединения с сервером
     */
    protected void disconnectListener() {
        removeDisconnectListener();
        transferDisconnectListener = new TransferDisconnectListener(tid, this, callback);
        transferRegistryListener = new TransferRegistryListener(tid, this, callback);

        socket.on(Socket.EVENT_DISCONNECT, transferDisconnectListener);
    }

    /**
     * Удаление слушителя о соединении с сервером
     */
    private void removeDisconnectListener() {

        if (transferRegistryListener != null) {
            transferRegistryListener = null;
        }

        if (transferDisconnectListener != null) {
            socket.off(Socket.EVENT_DISCONNECT, transferDisconnectListener);
            transferDisconnectListener = null;
        }

    }

    /**
     * Размер блока для отправки на сервер
     *
     * @return возвращается размер
     */
    protected int getChunk() {
        if (STATUS_TRANSFER_SPEED) {
            return CHUNK;
        } else {
            return (int) calcChunk;
        }
    }

    /**
     * обновление размера блока
     *
     * @param chunk размер блока
     */
    protected void updateChunk(final long chunk) {
        if (chunk > 0) {
            calcChunk = chunk;
            Log.e("pu", "update chunk: " + calcChunk);
        } else {
            if (calcChunk + chunk > 0) {
                calcChunk = calcChunk + chunk;
                Log.e("pu", "bad update chunk: " + calcChunk);
            }
            Log.e("pu", "after bad update: " + calcChunk);
        }
    }

    /**
     * очистка данных
     */
    public void destroy() {
        removeDisconnectListener();
        removeListener();
    }

    /**
     * удаление слушателя
     */
    abstract void removeListener();

    /**
     * перезапуск процесса
     */
    abstract void restart();

    /**
     * Является ли UploadTransfer или DownloadTransfer
     */
    abstract public boolean isUpload();

    /**
     * обработчик подключения (регистрации на) к серверу
     */
    private class TransferRegistryListener extends TransferListener {
        /**
         * конструктор
         *
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public TransferRegistryListener(final @NonNull String tid,
                                        final @NonNull Transfer transfer,
                                        final @Nullable ITransferStatusCallback statusCallback) {
            super(synchronization, tid, transfer, statusCallback);
        }

        @Override
        public void call(Object... args) {
            try {
                onRestart();
                transfer.restart();
            } catch (Exception e) {
                onError(StringUtil.defaultEmptyString(e.getMessage()));
            }
        }
    }

    /**
     * обработчик потери подключения к серверу
     */
    class TransferDisconnectListener extends TransferListener {
        /**
         * конструктор
         *
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public TransferDisconnectListener(final @NonNull String tid,
                                          final @NonNull Transfer transfer,
                                          final @Nullable ITransferStatusCallback statusCallback) {
            super(synchronization, tid, transfer, statusCallback);
        }

        @Override
        public void call(Object... args) {
            try {
                onStop();
                transfer.removeListener();
            } catch (Exception e) {
                onError(StringUtil.defaultEmptyString(e.getMessage()));
            }
        }
    }
}
