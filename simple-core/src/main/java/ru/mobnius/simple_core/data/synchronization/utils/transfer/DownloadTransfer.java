package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Date;

import io.socket.client.Socket;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.synchronization.OnSynchronizationListeners;
import ru.mobnius.simple_core.utils.StringUtil;

public class DownloadTransfer extends Transfer {
    /**
     * слушатель загрузки данных на клиент
     */
    @Nullable
    private DownloadTransfer.DownloadListener downloadListener;

    /**
     * массив байтов с загруженными данными
     */
    @Nullable
    private byte[] downloadBytes;

    @NonNull
    private final Date dtStart;

    /**
     * текущая позиция при загрузке на клиент
     */
    int downloadPosition = 0;

    public DownloadTransfer(final @NonNull OnSynchronizationListeners synchronization,
                            final @NonNull ITransferStatusCallback callback,
                            final @NonNull Socket socket,
                            final @NonNull String version,
                            final @NonNull String tid) {
        super(synchronization, callback, socket, version, tid);
        dtStart = new Date();
    }

    /**
     * загрузка на клиент
     */
    public void download() {

        disconnectListener();

        downloadListener = new DownloadListener(synchronization, tid, this, callback);
        downloadListener.onStart();
        socket.on(EVENT_DOWNLOAD, downloadListener);

        socket.emit(EVENT_DOWNLOAD, protocolVersion, downloadPosition, getChunk(), tid);
    }

    /**
     * перезапуск процесса
     */
    public void restart() {
        downloadListener = new DownloadTransfer.DownloadListener(synchronization, tid, this, callback);
        socket.on(EVENT_DOWNLOAD, downloadListener);

        socket.emit(EVENT_DOWNLOAD, protocolVersion, downloadPosition, getChunk(), tid);
    }

    @Override
    public boolean isUpload() {
        return false;
    }

    /**
     * Удаление слушателя
     */
    public void removeListener() {
        if (downloadListener != null) {
            socket.off(EVENT_DOWNLOAD, downloadListener);
            downloadListener = null;
        }

    }

    @Override
    public void destroy() {
        super.destroy();

        downloadBytes = null;
        downloadPosition = 0;
    }

    class DownloadListener extends TransferListener {
        /**
         * конструктор
         *
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public DownloadListener(OnSynchronizationListeners synchronization, String tid, DownloadTransfer transfer, ITransferStatusCallback statusCallback) {
            super(synchronization, tid, transfer, statusCallback);
        }

        /**
         * добавление элементов в массив
         *
         * @param a первый
         * @param b второй
         * @return результат соединения массивов
         */
        byte[] f(byte[] a, byte[] b) {
            byte[] c = new byte[a.length + b.length];
            System.arraycopy(a, 0, c, 0, a.length);
            System.arraycopy(b, 0, c, a.length, b.length);
            return c;
        }

        @Override
        public void call(final Object... args) {
            if (synchronization.getEntities(tid).length > 0) {
                try {
                    processing(args);
                } catch (Exception e) {
                    onError(StringUtil.defaultEmptyString(e.getMessage()));
                }
            }
        }

        /**
         * обработка результат
         *
         * @param args параметры
         */
        void processing(Object[] args) {
            final boolean valid = args != null && args[0] instanceof JSONObject;

            if (!valid) {
                onError(BaseApp.SENDED_RESULT_IS_NOT_JSON);
                return;
            }

            final JSONObject jsonObject = (JSONObject) args[0];
            final TransferResult result = TransferResult.readResult(jsonObject);
            if (result.tid.equals(tid)) {
                if (!result.data.success) {
                    onError(result.data.msg);
                } else {
                    if (downloadBytes == null) {
                        downloadBytes = new byte[0];
                    }

                    downloadBytes = f(downloadBytes, result.result);

                    if (result.meta.processed) {
                        onEnd(downloadBytes);
                        transfer.destroy();
                    } else {
                        downloadPosition = result.meta.start;
                        int percent = (int) (((long) downloadPosition * 100) / (long) result.meta.totalLength);
                        socket.emit(EVENT_DOWNLOAD, protocolVersion, downloadPosition, getChunk(), tid);

                        long lashChunk = getChunk();
                        Log.e("pu", "last chunk: " + lashChunk);
                        // время которое потребовалось для передачи CHUNK блока
                        long time = new Date().getTime() - getIterationStartTime().getTime();
                        Log.e("pu", "time: " + time);
                        if (time == 0)
                            time = 1;
                        // сколько нужно блоков для передачи за 1 секунду?
                        updateChunk(((long) INTERVAL * getChunk()) / time);

                        onPercent(percent,
                                TransferSpeed.getInstance(lashChunk, new Date().getTime() - getIterationStartTime().getTime()),
                                getLastTime(dtStart, percent),
                                TransferData.getInstance(downloadPosition, result.meta.totalLength));
                    }
                }
            }
        }
    }
}
