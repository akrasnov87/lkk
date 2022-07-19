package ru.mobnius.simple_core.data.synchronization.utils.transfer;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

import io.socket.client.Socket;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.synchronization.OnSynchronizationListeners;
import ru.mobnius.simple_core.utils.StringUtil;

public class UploadTransfer extends Transfer {

    /**
     * слушатель загрузки данных на сервер
     */
    @Nullable
    private UploadTransfer.UploadListener uploadListener;

    /**
     * массив байтов который требуется передать на сервер
     */
    @NonNull
    private final byte[] uploadBytes;
    @NonNull
    private final Date dtStart;
    /**
     * текущая позиция при загрузки на сервер
     */
    private int uploadPosition = 0;


    public UploadTransfer(final @NonNull OnSynchronizationListeners synchronization,
                          final @NonNull ITransferStatusCallback callback,
                          final @NonNull Socket socket,
                          final @NonNull String version,
                          final @NonNull String tid,
                          final @NonNull byte[] uploadBytes) {
        super(synchronization, callback, socket, version, tid);
        dtStart = new Date();
        this.uploadBytes = uploadBytes;
    }

    /**
     * загрузка на сервер информации
     */
    public void upload() {
        disconnectListener();

        uploadListener = new UploadListener(synchronization, tid, this, callback);
        uploadListener.onStart();
        socket.on(EVENT_UPLOAD, uploadListener);
        socket.emit(EVENT_UPLOAD, protocolVersion, Arrays.copyOfRange(uploadBytes, uploadPosition, getChunk()), tid, uploadPosition, uploadBytes.length);
    }

    /**
     * перезапуск процесса
     */
    public void restart() {
        uploadListener = new UploadTransfer.UploadListener(synchronization, tid, this, callback);
        socket.on(EVENT_UPLOAD, uploadListener);

        int end = uploadPosition + getChunk();
        if (end > uploadBytes.length) {
            end = uploadBytes.length;
        }

        socket.emit(EVENT_UPLOAD, protocolVersion, Arrays.copyOfRange(uploadBytes, uploadPosition, end), tid, uploadPosition, uploadBytes.length);
    }

    @Override
    public boolean isUpload() {
        return true;
    }

    /**
     * Удаление слушателя
     */
    public void removeListener() {
        if (uploadListener != null) {
            socket.off(EVENT_UPLOAD, uploadListener);
            uploadListener = null;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        uploadPosition = 0;
    }

    private class UploadListener extends TransferListener {

        /**
         * конструктор
         *
         * @param synchronization синхронизация
         * @param tid             идентификатор транзакции
         * @param instance        текущий экземпляр передачи данных
         * @param statusCallback  статус
         */
        public UploadListener(final @NonNull OnSynchronizationListeners synchronization,
                              final @NonNull String tid,
                              final @NonNull UploadTransfer instance,
                              final @NonNull ITransferStatusCallback statusCallback) {
            super(synchronization, tid, instance, statusCallback);
        }

        @Override
        public void call(final @Nullable Object... args) {
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
        void processing(final @Nullable Object[] args) {
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
                    if (result.meta.processed) {
                        onEnd(uploadBytes);
                        transfer.destroy();
                    } else {
                        uploadPosition = result.meta.start;
                        int percent = (int) (((long) uploadPosition * 100) / (long) uploadBytes.length);
                        int end = uploadPosition + getChunk();
                        if (end > uploadBytes.length) {
                            end = uploadBytes.length;
                        }
                        try {
                            socket.emit(EVENT_UPLOAD, protocolVersion, Arrays.copyOfRange(uploadBytes, uploadPosition, end), tid, uploadPosition, uploadBytes.length);
                        } catch (Exception e) {
                            onError(StringUtil.defaultEmptyString(e.getMessage()));
                        }
                        long lastChunk = getChunk();
                        Log.e("pu", "upload last chunk: " + lastChunk);
                        // время которое потребовалось для передачи CHUNK блока
                        long time = new Date().getTime() - getIterationStartTime().getTime();
                        Log.e("pu", "upload time: " + time);
                        // сколько нужно блоков для передачи за 1 секунду?
                        updateChunk(((long) INTERVAL * getChunk()) / (time == 0 ? 1 : time));
                        onPercent(percent,
                                TransferSpeed.getInstance(lastChunk, new Date().getTime() - getIterationStartTime().getTime()),
                                getLastTime(dtStart, percent),
                                TransferData.getInstance(uploadPosition, uploadBytes.length));

                    }
                }
            }
        }
    }
}
