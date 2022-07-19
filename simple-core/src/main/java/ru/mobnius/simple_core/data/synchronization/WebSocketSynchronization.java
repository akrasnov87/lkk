package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.greendao.AbstractDaoSession;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.data.socket.SocketManager;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.DownloadTransfer;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.ITransferStatusCallback;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.Transfer;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.TransferListener;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.TransferProgress;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.UploadTransfer;
import ru.mobnius.simple_core.utils.JsonUtil;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.SyncUtil;

/**
 * Механизм обработки синхронизации через websocket
 */
public abstract class WebSocketSynchronization extends BaseSynchronization {
    private final static String SYNCHRONIZATION = "synchronization";
    private final static String SYNCHRONIZATION_STATUS = "synchronization-status";
    /**
     * Обработчик ответов от websocket
     */
    @Nullable
    private WebSocketSynchronization.SynchronizationListener synchronizationListener;
    @Nullable
    private SynchronizationConnectListener synchronizationConnectListener;

    /**
     * Хранилище для объектов по приемке и отдаче пакетов
     */
    @NonNull
    private final HashMap<String, Transfer> transfers;
    @NonNull
    private final List<EndTransferResult> mEndTransferResults;

    /**
     * Конструктор
     *
     * @param session сессия для подключения к БД
     */
    public WebSocketSynchronization(final @NonNull String version,
                                    final @NonNull AbstractDaoSession session,
                                    final boolean zip) {
        super(version, session, zip);
        transfers = new HashMap<>();
        mEndTransferResults = new ArrayList<>();
    }

    @Override
    public void start(final @NonNull IProgress progress) {
        super.start(progress);

        onProgress(IProgressStep.START, BaseApp.CHECKING_SERVER_CONNECTION);

        if (SocketManager.getInstance() == null
                || SocketManager.getInstance().socket == null
                || !SocketManager.getInstance().isRegistered()) {
            onError(IProgressStep.START, BaseApp.WEBSOCKET_CONNECTION_UNAVAILABLE, null);
            stop();
            return;
        }

        synchronizationListener = new WebSocketSynchronization.SynchronizationListener();
        synchronizationConnectListener = new SynchronizationConnectListener();

        SocketManager.getInstance().socket.on(Socket.EVENT_DISCONNECT, synchronizationConnectListener);
        SocketManager.getInstance().socket.on(Socket.EVENT_CONNECT, synchronizationConnectListener);
        SocketManager.getInstance().socket.on(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);

        SocketManager.getInstance().socket.on(SYNCHRONIZATION, synchronizationListener);
        SocketManager.getInstance().socket.on(SYNCHRONIZATION_STATUS, synchronizationListener);

        // устанавливаем идентификаторы
        for (final Entity entity : getEntityToList()) {

            if (!SyncUtil.updateTid(this, entity.tableName, entity.tid)) {
                onError(IProgressStep.START, BaseApp.TID_NULL_ASSIGNMENT_ERROR + entity.tableName, entity.tid);
                stop();
                return;
            }
        }

    }

    @Override
    protected void sendBytes(final @NonNull String tid, final @NonNull byte[] bytes) {
        final ITransferStatusCallback callback = new ITransferStatusCallback() {
            @Override
            public void onStartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.START, tid, transfer, null);
            }

            @Override
            public void onRestartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.RESTART, tid, transfer, null);
            }

            @Override
            public void onPercentTransfer(@NonNull String tid, @NonNull TransferProgress progress, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.PERCENT, tid, transfer, progress);
            }

            @Override
            public void onStopTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.STOP, tid, transfer, null);
            }

            @Override
            public void onEndTransfer(@NonNull String tid, @NonNull Transfer transfer, @Nullable Object data) {
                onProgressTransfer(TransferListener.END, tid, transfer, data);
                if (SocketManager.getInstance() == null || SocketManager.getInstance().socket == null) {
                    return;
                }
                SocketManager.getInstance().socket.emit(SYNCHRONIZATION, tid, PreferencesManager.SYNC_PROTOCOL_v2);
            }

            @Override
            public void onErrorTransfer(@NonNull String tid, @NonNull Transfer transfer, @NonNull String message) {
                onProgressTransfer(TransferListener.ERROR, tid, transfer, message);
                onError(IProgressStep.UPLOAD, message, tid);
            }
        };
        if (SocketManager.getInstance() == null || SocketManager.getInstance().socket == null) {
            return;
        }
        final UploadTransfer uploadTransfer = new UploadTransfer(this,
                callback,
                SocketManager.getInstance().socket,
                PreferencesManager.SYNC_PROTOCOL_v2,
                tid,
                bytes);
        transfers.put(tid, uploadTransfer);
        uploadTransfer.upload();
    }

    @Override
    protected void sendBytes(final @NonNull String tid,
                             final @NonNull byte[] bytes,
                             final @NonNull FileTransferStartCallback fileTransferStartCallback) {
        final ITransferStatusCallback callback = new ITransferStatusCallback() {
            @Override
            public void onStartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.START, tid, transfer, null);
            }

            @Override
            public void onRestartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.RESTART, tid, transfer, null);
            }

            @Override
            public void onPercentTransfer(@NonNull String tid, @NonNull TransferProgress progress, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.PERCENT, tid, transfer, progress);
            }

            @Override
            public void onStopTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                onProgressTransfer(TransferListener.STOP, tid, transfer, null);
            }

            @Override
            public void onEndTransfer(@NonNull String tid, @NonNull Transfer transfer, @Nullable Object data) {
                onProgressTransfer(TransferListener.END, tid, transfer, data);
                if (SocketManager.getInstance() == null || SocketManager.getInstance().socket == null) {
                    return;
                }
                SocketManager.getInstance().socket.emit(SYNCHRONIZATION, tid, PreferencesManager.SYNC_PROTOCOL_v2);
                fileTransferStartCallback.onFileTransferCanStart();
            }

            @Override
            public void onErrorTransfer(@NonNull String tid, @NonNull Transfer transfer, @NonNull String message) {
                onProgressTransfer(TransferListener.ERROR, tid, transfer, message);
                onError(IProgressStep.UPLOAD, message, tid);
            }
        };
        if (SocketManager.getInstance() == null || SocketManager.getInstance().socket == null) {
            return;
        }
        final UploadTransfer uploadTransfer = new UploadTransfer(this,
                callback,
                SocketManager.getInstance().socket,
                PreferencesManager.SYNC_PROTOCOL_v2,
                tid,
                bytes);
        transfers.put(tid, uploadTransfer);

        uploadTransfer.upload();
    }

    @Override
    public void stop() {
        if (synchronizationListener != null && SocketManager.getInstance() != null && SocketManager.getInstance().socket != null) {
            SocketManager.getInstance().socket.off(SYNCHRONIZATION, synchronizationListener);
            SocketManager.getInstance().socket.off(SYNCHRONIZATION_STATUS, synchronizationListener);
        }

        if (synchronizationListener != null && SocketManager.getInstance() != null && SocketManager.getInstance().socket != null) {
            SocketManager.getInstance().socket.off(Socket.EVENT_DISCONNECT, synchronizationConnectListener);
            SocketManager.getInstance().socket.off(Socket.EVENT_CONNECT, synchronizationConnectListener);
            SocketManager.getInstance().socket.off(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);
        }

        for (final Transfer t : transfers.values()) {
            t.destroy();
        }
        transfers.clear();
        mEndTransferResults.clear();

        super.stop();
    }


    @Override
    public void cancel() {
        if (synchronizationListener != null && SocketManager.getInstance() != null && SocketManager.getInstance().socket != null) {
            SocketManager.getInstance().socket.off(SYNCHRONIZATION, synchronizationListener);
            SocketManager.getInstance().socket.off(SYNCHRONIZATION_STATUS, synchronizationListener);
        }

        if (synchronizationListener != null && SocketManager.getInstance() != null && SocketManager.getInstance().socket != null) {
            SocketManager.getInstance().socket.off(Socket.EVENT_DISCONNECT, synchronizationConnectListener);
            SocketManager.getInstance().socket.off(Socket.EVENT_CONNECT, synchronizationConnectListener);
            SocketManager.getInstance().socket.off(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);
        }

        for (final Transfer t : transfers.values()) {
            t.destroy();
        }
        transfers.clear();
        mEndTransferResults.clear();
        super.cancel();
    }

    /**
     * обработчик транспортировки данных
     *
     * @param part     тип операции
     * @param tid      идентификатор транзакции
     * @param transfer объект транспортировки
     * @param data     данные
     */
    protected void onProgressTransfer(final int part,
                                      final @NonNull String tid,
                                      final @NonNull Transfer transfer,
                                      final @Nullable Object data) {
        if (iProgress == null) {
            return;
        }
        switch (part) {
            case TransferListener.START:
                iProgress.onStartTransfer(tid, transfer);
                break;

            case TransferListener.RESTART:
                iProgress.onRestartTransfer(tid, transfer);
                break;

            case TransferListener.PERCENT:
                TransferProgress transferProgress = TransferProgress.getEmptyInstance();
                if (data instanceof TransferProgress) {
                    transferProgress = (TransferProgress) data;
                }
                iProgress.onPercentTransfer(tid, transferProgress, transfer);
                break;

            case TransferListener.STOP:
                iProgress.onStopTransfer(tid, transfer);
                break;

            case TransferListener.END:
                iProgress.onEndTransfer(tid, transfer, data);
                break;

            case TransferListener.ERROR:
                String error = StringUtil.EMPTY;
                if (data instanceof String) {
                    error = (String) data;
                }
                iProgress.onErrorTransfer(tid, transfer, error);
                break;
        }
    }

    /**
     * текущая синхронизация. Нужно для вызова в других контекстах
     *
     * @return текущая синхронизация
     */
    @NonNull
    private OnSynchronizationListeners getSynchronization() {
        return this;
    }

    @Override
    public void destroy() {
        super.destroy();
        mEndTransferResults.clear();
        transfers.clear();
        synchronizationListener = null;
    }

    private class SynchronizationConnectListener implements Emitter.Listener {

        @Override
        public void call(final @Nullable Object... args) {
            try {
                processing(args);
            } catch (Exception e) {
                e.printStackTrace();
                onError(IProgressStep.UPLOAD, e, null);
            }
        }

        /**
         * обработка результат
         *
         * @param args параметры
         */
        private void processing(final @Nullable Object[] args) {
            if (args != null && args.length > 0) {
                final Object item = args[0];
                if (item instanceof String) {
                    onProgress(IProgressStep.NONE, (String) item);
                } else if (item instanceof SocketIOException) {
                    final SocketIOException socketIOException = (SocketIOException) item;
                    onError(IProgressStep.NONE, StringUtil.defaultEmptyString(socketIOException.getMessage()), null);
                } else if (item instanceof EngineIOException) {
                    final EngineIOException engineIOException = (EngineIOException) item;
                    onError(IProgressStep.NONE, StringUtil.defaultEmptyString(engineIOException.getMessage()), null);
                } else if (item instanceof Integer) {
                    onProgress(IProgressStep.NONE, String.valueOf(item));
                }
            } else {
                onProgress(IProgressStep.NONE, BaseApp.ERROR_ENG);
            }
        }
    }

    /**
     * обработкчик socket
     */
    private class SynchronizationListener implements Emitter.Listener {

        @Override
        public void call(final @Nullable Object... args) {
            try {
                processing(args);
            } catch (Exception e) {
                e.printStackTrace();
                onError(IProgressStep.UPLOAD, e, null);
            }
        }

        /**
         * обработка результат
         *
         * @param args параметры
         */
        private void processing(final @Nullable Object[] args) {
            if (SocketManager.getInstance() == null || SocketManager.getInstance().socket == null) {
                onError(IProgressStep.RESTORE, BaseApp.SOCKET_PROBLEM, null);
                return;
            }
            if (args == null || args.length == 0 || !(args[0] instanceof JSONObject)) {
                onError(IProgressStep.RESTORE, BaseApp.SENDED_RESULT_IS_NOT_JSON, null);
                return;
            }
            final JSONObject jsonObject = (JSONObject) args[0];
            // данный кусок кода нужен для вывода сообщений от сервера см. modules/synchronization/v1.js метод socketSend
            final String objectTid = JsonUtil.getNullableString(jsonObject, JsonUtil.TID_JSON_KEY);
            if (StringUtil.isNotEmpty(objectTid)) {
                if (getEntities(objectTid).length > 0) {
                    final String result = JsonUtil.getNullableString(jsonObject, JsonUtil.RESULT_JSON_KEY);
                    if (StringUtil.isNotEmpty(result)) {
                        return;
                    }
                }
            }

            final JSONObject jsonData = JsonUtil.getNullableJsonObject(jsonObject, JsonUtil.DATA_JSON_KEY);
            if (jsonData == null) {
                onError(IProgressStep.RESTORE, BaseApp.NO_JSON_KEY_OR_OBJECT + JsonUtil.DATA_JSON_KEY, null);
                stop();
                return;
            }
            final boolean success = JsonUtil.getBooleanFromJSONObject(jsonData, JsonUtil.SUCCESS_JSON_KEY, false);
            if (!success) {
                String errorMessage = BaseApp.NO_JSON_KEY_OR_OBJECT + JsonUtil.SUCCESS_JSON_KEY;
                if (jsonData.has(JsonUtil.MSG_JSON_KEY)) {
                    errorMessage = JsonUtil.getNonNullDefaultString(jsonData, JsonUtil.MSG_JSON_KEY, errorMessage);
                }
                onError(IProgressStep.RESTORE, errorMessage, null);
                stop();
                return;
            }
            // тут нужно запросить данные от сервера
            final JSONObject metaJSONObject = JsonUtil.getNullableJsonObject(jsonObject, JsonUtil.META_JSON_KEY);
            if (metaJSONObject == null) {
                onError(IProgressStep.RESTORE, BaseApp.NO_JSON_KEY_OR_OBJECT + JsonUtil.META_JSON_KEY, null);
                stop();
                return;
            }
            final String tid = JsonUtil.getNullableString(metaJSONObject, JsonUtil.TID_JSON_KEY);
            if (StringUtil.isEmpty(tid)) {
                onError(IProgressStep.RESTORE, BaseApp.NO_JSON_KEY_OR_OBJECT + JsonUtil.TID_JSON_KEY, null);
                stop();
                return;
            }
            final boolean processed = JsonUtil.getBooleanFromJSONObject(metaJSONObject, JsonUtil.PROCESSED_JSON_KEY, false);
            if (!processed || getEntities(tid).length == 0) {
                onError(IProgressStep.RESTORE, BaseApp.NO_JSON_KEY_OR_OBJECT + JsonUtil.PROCESSED_JSON_KEY + getEntities(tid).length, null);
                stop();
                return;
            }

            final ITransferStatusCallback callback = new ITransferStatusCallback() {
                @Override
                public void onStartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                    onProgressTransfer(TransferListener.START, tid, transfer, null);
                }

                @Override
                public void onRestartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                    onProgressTransfer(TransferListener.RESTART, tid, transfer, null);
                }

                @Override
                public void onPercentTransfer(@NonNull String tid, @NonNull TransferProgress progress, @NonNull Transfer transfer) {
                    onProgressTransfer(TransferListener.PERCENT, tid, transfer, progress);
                }

                @Override
                public void onStopTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                    onProgressTransfer(TransferListener.STOP, tid, transfer, null);
                }

                @Override
                public void onEndTransfer(@NonNull final String tid, @NonNull final Transfer transfer, @Nullable final Object data) {
                    mEndTransferResults.add(new EndTransferResult(tid, transfer, data));
                    // значит все пакеты приняты и нужно их обработать
                    if (mEndTransferResults.size() != transfers.size()) {
                        return;
                    }
                    try {
                        for (final EndTransferResult result : mEndTransferResults) {
                            byte[] arr = null;
                            try {
                                arr = (byte[]) result.object;
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            }
                            if (arr == null) {
                                arr = new byte[0];
                            }
                            processingPackage(getCollectionTid(), arr);
                            onProgressTransfer(TransferListener.END, result.tid, result.transfer, null);
                            if (isEntityFinished()) {
                                stop();
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorTransfer(@NonNull String tid, @NonNull Transfer
                        transfer, @NonNull String message) {
                    onProgressTransfer(TransferListener.ERROR, tid, transfer, message);
                    onError(IProgressStep.DOWNLOAD, message, tid);
                }
            };
            final DownloadTransfer downloadTransfer = new DownloadTransfer(getSynchronization(),
                    callback,
                    SocketManager.getInstance().socket,
                    PreferencesManager.SYNC_PROTOCOL_v2,
                    tid);
            final Transfer transfer = transfers.get(tid);
            if (transfer != null) {
                transfer.destroy();
            }
            transfers.put(tid, downloadTransfer);
            downloadTransfer.download();
        }
    }
}
