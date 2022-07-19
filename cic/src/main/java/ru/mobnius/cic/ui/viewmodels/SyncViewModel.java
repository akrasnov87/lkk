package ru.mobnius.cic.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.RoutesReceivedTask;
import ru.mobnius.cic.data.manager.ManualSynchronization;
import ru.mobnius.cic.ui.model.SyncLogItem;
import ru.mobnius.cic.ui.model.SyncPercentageProgress;
import ru.mobnius.cic.ui.model.SyncProgressItem;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.data.socket.SocketConnectionInfo;
import ru.mobnius.simple_core.data.socket.SocketEventListener;
import ru.mobnius.simple_core.data.socket.SocketManager;
import ru.mobnius.simple_core.data.synchronization.Entity;
import ru.mobnius.simple_core.data.synchronization.IProgress;
import ru.mobnius.simple_core.data.synchronization.IProgressStep;
import ru.mobnius.simple_core.data.synchronization.OnSynchronizationListeners;
import ru.mobnius.simple_core.data.synchronization.utils.SocketStatusReader;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.DownloadTransfer;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.Transfer;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.TransferListener;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.TransferProgress;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.UploadTransfer;
import ru.mobnius.simple_core.utils.StringUtil;

public class SyncViewModel extends ViewModel {

    public boolean isSyncSuccess = true;
    @Nullable
    public OnSynchronizationListeners syncListener;
    @Nullable
    private IProgress iProgress;
    @NonNull
    public final List<Boolean> successList = new ArrayList<>();
    @NonNull
    public final List<SyncProgressItem> progressItems = new ArrayList<>();
    public final List<SyncProgressItem> uploadItems = new ArrayList<>();
    public final List<SyncProgressItem> downloadItems = new ArrayList<>();
    @NonNull
    public final List<SyncLogItem> logItems = new ArrayList<>();
    @Nullable
    public MutableLiveData<SyncPercentageProgress> progressLiveData;
    @Nullable
    public MutableLiveData<List<SyncLogItem>> logLiveData;
    @NonNull
    public final MutableLiveData<Boolean> syncSuccessData = new MutableLiveData<>();
    @NonNull
    public final MutableLiveData<Boolean> circleProgressData = new MutableLiveData<>();
    @NonNull
    public final MutableLiveData<Boolean> startStopLoadingData = new MutableLiveData<>();
    @NonNull
    public final MutableLiveData<SocketConnectionInfo> socketConnectionData = new MutableLiveData<>();
    @NonNull
    public final MutableLiveData<Boolean> routesReceivedData = new MutableLiveData<>();

    public void init(final @NonNull String version) {
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return;
        }
        if (SocketManager.getInstance() != null) {
            SocketManager.getInstance().destroy();
        }
        final SocketEventListener listeners = new SocketEventListener() {
            @Override
            public void onConnect() {
                socketConnectionData.postValue(new SocketConnectionInfo(false, false, MobniusApplication.CONNECTION_ESTABLISHED));
            }

            @Override
            public void onConnectError(final @NonNull String error) {
                socketConnectionData.postValue(new SocketConnectionInfo(false, true, MobniusApplication.CONNECTION_ERROR + error));
            }

            @Override
            public void onDisconnect() {
                socketConnectionData.postValue(new SocketConnectionInfo(false, false, MobniusApplication.CONNECTION_LOST));
            }
        };
        SocketManager.createInstance(GlobalSettings.getConnectUrl(),
                Authorization.getInstance().user.getCredentials(),
                listeners);
        socketConnectionData.setValue(new SocketConnectionInfo(true, false, MobniusApplication.ESTABLISHING_CONNECTION));
        if (PreferencesManager.getInstance() == null) {
            return;
        }
        if (syncListener == null) {
            syncListener = ManualSynchronization.getInstance(version, PreferencesManager.getInstance().getZip());
        }
        if (progressLiveData == null) {
            progressLiveData = new MutableLiveData<>();
        }
        if (logLiveData == null) {
            logLiveData = new MutableLiveData<>();
        }

    }


    @NonNull
    private IProgress getIProgress() {
        if (iProgress != null) {
            iProgress = null;
        }
        iProgress = new IProgress() {
            @Override
            public void onStartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                circleProgressData.postValue(false);
                if (transfer instanceof UploadTransfer) {
                    progressItems.add(new SyncProgressItem(tid, TransferListener.START,
                            0, 0, getNameForTid(tid), StringUtil.EMPTY, StringUtil.EMPTY));

                    uploadItems.add(new SyncProgressItem(tid, TransferListener.START,
                            0, 0, getNameForTid(tid), StringUtil.EMPTY, StringUtil.EMPTY));
                    successList.add(true);
                }

                if (transfer instanceof DownloadTransfer) {
                    downloadItems.add(new SyncProgressItem(tid, TransferListener.START,
                            0, 0, getNameForTid(tid), StringUtil.EMPTY, StringUtil.EMPTY));
                }
                if (progressLiveData == null) {
                    return;
                }
                final SyncPercentageProgress percentageProgress =
                        new SyncPercentageProgress(IProgressStep.START,
                                0,
                                transfer.isUpload(),
                                false,
                                StringUtil.EMPTY);
                progressLiveData.postValue(percentageProgress);
            }

            @Override
            public void onRestartTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                if (progressLiveData == null) {
                    return;
                }
                final int index = getProgressItemIndexByTid(tid);
                if (index < 0) {
                    return;
                }
                final SyncProgressItem syncProgressItem = new SyncProgressItem(tid, TransferListener.RESTART,
                        0, 0, getNameForTid(tid), StringUtil.EMPTY, StringUtil.EMPTY);
                progressItems.set(index, syncProgressItem);
                progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.RESTORE,
                        0,
                        transfer.isUpload(),
                        false,
                        StringUtil.EMPTY));
            }

            @Override
            public void onPercentTransfer(@NonNull String tid, @NonNull TransferProgress progress, @NonNull Transfer transfer) {
                if (progressLiveData == null) {
                    return;
                }
                final int index = getProgressItemIndexByTid(tid);
                if (index < 0) {
                    return;
                }
                if (transfer instanceof UploadTransfer) {
                    int downloadPercentage = 0;
                    if (index < progressItems.size()) {
                        downloadPercentage = progressItems.get(index).downloadProgress;

                    }
                    final SyncProgressItem uploadProgressItem = new SyncProgressItem(tid, TransferListener.PERCENT, (int) progress.percent,
                            downloadPercentage, getNameForTid(tid), progress.transferData.toString(), progress.toString());
                    progressItems.set(index, uploadProgressItem);
                }

                if (transfer instanceof DownloadTransfer) {
                    int uploadPercentage = 0;
                    if (index < progressItems.size()) {
                        uploadPercentage = progressItems.get(index).uploadProgress;

                    }
                    final SyncProgressItem downloadProgressItem = new SyncProgressItem(tid, TransferListener.PERCENT, uploadPercentage,
                            (int) progress.percent, getNameForTid(tid), progress.transferData.toString(), progress.toString());
                    progressItems.set(index, downloadProgressItem);
                }

                int percentage = 0;
                for (final SyncProgressItem progressItem : progressItems) {
                    percentage += progressItem.uploadProgress + progressItem.downloadProgress;
                }
                progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.PERCENTAGE,
                        percentage,
                        transfer.isUpload(),
                        false,
                        StringUtil.EMPTY));
            }

            @Override
            public void onStopTransfer(@NonNull String tid, @NonNull Transfer transfer) {
                final int index = getProgressItemIndexByTid(tid);
                if (index < 0) {
                    return;
                }
                final SyncProgressItem syncProgressItem = new SyncProgressItem(tid, TransferListener.STOP, 0,
                        0, getNameForTid(tid), StringUtil.EMPTY, StringUtil.EMPTY);
                progressItems.set(index, syncProgressItem);
                if (progressLiveData == null) {
                    return;
                }
                progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.STOP,
                        0,
                        transfer.isUpload(),
                        false,
                        StringUtil.EMPTY));
            }

            @Override
            public void onEndTransfer(@NonNull String tid, @NonNull Transfer transfer, @Nullable Object data) {
                if (transfer instanceof DownloadTransfer) {
                    final int removeIndex = getProgressItemRemoveIndexByTid(tid);
                    if (removeIndex < 0) {
                        return;
                    }
                    progressItems.remove(removeIndex);
                    successList.remove(0);
                    if (progressLiveData == null) {
                        return;
                    }
                    if (successList.size() == 0) {
                        circleProgressData.postValue(true);
                    }
                    progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.END,
                            0,
                            transfer.isUpload(),
                            false,
                            StringUtil.EMPTY));
                }
            }

            @Override
            public void onErrorTransfer(@NonNull String tid, @NonNull Transfer transfer, @NonNull String message) {
                final int index = getProgressItemIndexByTid(tid);
                if (index < 0) {
                    return;
                }
                final SyncProgressItem syncProgressItem = new SyncProgressItem(tid, TransferListener.ERROR,
                        0, 0, getNameForTid(tid), StringUtil.EMPTY, StringUtil.EMPTY);
                progressItems.set(index, syncProgressItem);
                if (progressLiveData == null) {
                    return;
                }
                int percentage = 0;
                for (final SyncProgressItem progressItem : progressItems) {
                    percentage += progressItem.uploadProgress;
                }
                progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.ERROR,
                        percentage,
                        transfer.isUpload(),
                        false,
                        message));
            }

            @Override
            public void onStop(@NonNull OnSynchronizationListeners synchronization) {
                syncSuccessData.postValue(isSyncSuccess);
                if (progressLiveData == null) {
                    return;
                }
                progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.FINISH,
                        0,
                        false,
                        false,
                        StringUtil.EMPTY));

            }

            @Override
            public void onProgress(@NonNull OnSynchronizationListeners synchronization, int step, @NonNull String message, @Nullable String tid) {
                isSyncSuccess = true;
                Collections.reverse(logItems);
                logItems.add(new SyncLogItem(message, false));
                Collections.reverse(logItems);
                if (logLiveData != null) {
                    logLiveData.postValue(new ArrayList<>(logItems));
                }

                final SocketStatusReader reader = SocketStatusReader.getInstance(message);
                if (reader == null || reader.params.length == 0) {
                    return;
                }
                if (tid == null) {
                    return;
                }
                final int index = getProgressItemIndexByTid(tid);
                if (index < 0) {
                    return;
                }
                final SyncProgressItem existing = progressItems.get(index);
                final SyncProgressItem updated = new SyncProgressItem(tid, existing.type,
                        existing.uploadProgress, existing.downloadProgress, existing.name, reader.params[0], existing.status);
                progressItems.set(index, updated);
                if (progressLiveData == null) {
                    return;
                }

                int percentage = 0;
                for (final SyncProgressItem progressItem : progressItems) {
                    percentage += progressItem.uploadProgress;
                }
                progressLiveData.postValue(new SyncPercentageProgress(IProgressStep.PROGRESS,
                        percentage,
                        false,
                        false,
                        message));
            }

            @Override
            public void onError(@NonNull OnSynchronizationListeners synchronization, int step, @NonNull String message, @Nullable String tid) {
                if (StringUtil.isNotEmpty(message)) {
                    isSyncSuccess = false;
                    Collections.reverse(logItems);
                    logItems.add(new SyncLogItem(message, true));
                    Collections.reverse(logItems);
                    if (logLiveData != null) {
                        logLiveData.postValue(new ArrayList<>(logItems));
                    }
                }
            }
        };
        return iProgress;
    }

    public void start(final @NonNull String version, final boolean isZip) {
        if (syncListener == null) {
            syncListener = ManualSynchronization.getInstance(version, isZip);
        }
        if (syncListener == null) {
            return;
        }
        circleProgressData.setValue(true);
        startStopLoadingData.setValue(true);
        syncListener.start(getIProgress());
    }

    @NonNull
    private String getNameForTid(final @NonNull String tid) {
        if (syncListener == null) {
            return StringUtil.EMPTY;
        }
        final Entity[] entities = syncListener.getEntities(tid);
        final String name;
        if (entities.length > 0) {
            name = entities[0].nameEntity;
        } else {
            name = MobniusApplication.UNKNOWN_ENG;
        }
        return name;
    }

    private int getProgressItemIndexByTid(final @NonNull String tid) {
        for (int i = 0; i < progressItems.size(); i++) {
            if (StringUtil.equals(progressItems.get(i).tid, tid)) {
                return i;
            }
        }
        return -1;
    }

    private int getProgressItemRemoveIndexByTid(final @NonNull String tid) {
        for (int i = 0; i < progressItems.size(); i++) {
            if (StringUtil.equals(progressItems.get(i).tid, tid)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    public MutableLiveData<SyncPercentageProgress> getProgressItems() {
        if (progressLiveData == null) {
            progressLiveData = new MutableLiveData<>();
            progressLiveData.setValue(new SyncPercentageProgress(IProgressStep.START,
                    0,
                    false,
                    false,
                    StringUtil.EMPTY));
        }

        return progressLiveData;
    }

    @NonNull
    public MutableLiveData<List<SyncLogItem>> getLogItems() {
        if (logLiveData == null) {
            logLiveData = new MutableLiveData<>();
            logLiveData.setValue(new ArrayList<>(logItems));
        }

        return logLiveData;
    }

    public void clearProgressItems() {
        progressItems.clear();
        if (progressLiveData == null) {
            return;
        }
        progressLiveData.setValue(new SyncPercentageProgress(IProgressStep.END,
                0,
                false,
                false,
                StringUtil.EMPTY));
    }

    public void clearLogItems() {
        logItems.clear();
        if (logLiveData == null) {
            return;
        }
        logLiveData.setValue(new ArrayList<>(logItems));
    }

    public void addStopLog(final @NonNull String stopMessage) {
        logItems.add(new SyncLogItem(stopMessage, false));
        if (logLiveData == null) {
            return;
        }
        logLiveData.setValue(new ArrayList<>(logItems));
    }

    @NonNull
    public MutableLiveData<Boolean> getRoutesReceived() {
        final MainTaskExecutor executor = new MainTaskExecutor();
        final RoutesReceivedTask routesReceivedTask = new RoutesReceivedTask();
        executor.executeAsync(routesReceivedTask, new MainTaskExecutorCallback<Void>() {
            @Override
            public void onCallableComplete(@NonNull Void result) {
                circleProgressData.postValue(false);
                startStopLoadingData.postValue(false);
                routesReceivedData.postValue(true);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                circleProgressData.postValue(false);
                startStopLoadingData.postValue(false);
                routesReceivedData.postValue(false);
            }
        });
        return routesReceivedData;
    }

    public void destroy() {
        iProgress = null;
        if (syncListener != null) {
            syncListener.stop();
            syncListener = null;
        }
        if (SocketManager.getInstance() != null) {
            SocketManager.getInstance().destroy();
        }
        successList.clear();

    }
}
