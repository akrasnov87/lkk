package ru.mobnius.simple_core.data.synchronization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.greendao.AbstractDaoSession;

import java.io.File;
import java.io.IOException;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.data.synchronization.meta.TableQuery;
import ru.mobnius.simple_core.data.synchronization.utils.FullServerSidePackage;
import ru.mobnius.simple_core.data.synchronization.utils.PackageResult;
import ru.mobnius.simple_core.utils.FileUtil;
import ru.mobnius.simple_core.utils.PackageCreateUtils;
import ru.mobnius.simple_core.utils.PackageReadUtils;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * синхронизация по передачи данных по websocket с вложениями
 */
public abstract class FileTransferWebSocketSynchronization
        extends WebSocketSynchronization {

    /**
     * идентификтаор транзакций для вложений
     */
    @Nullable
    public String fileTid;

    /**
     * применяются вложения или нет
     */
    protected boolean useAttachments = false;

    /**
     * конструктор
     *
     * @param session сессия для подключения к БД
     */
    public FileTransferWebSocketSynchronization(final @NonNull String version,
                                                final @NonNull AbstractDaoSession session,
                                                final boolean zip) {
        super(version, session, zip);
    }

    @Override
    public void start(final @NonNull IProgress progress) {
        super.start(progress);
    }

    @NonNull
    @Override
    public byte[] generatePackage(final @NonNull String tid) throws IOException {
        final PackageCreateUtils utils = new PackageCreateUtils(isZip);
        for (final Entity entity : entityList) {
            if (StringUtil.notEquals(entity.tid, tid)) {
                continue;
            }
            if (StringUtil.equals(tid, fileTid) && useAttachments) {
                // тут только обрабатывается добавление
                final Object[] records = getRecords(entity.tableName, tid).toArray();
                if (records.length > 0) {

                    for (final Object record : records) {
                        if (record instanceof OnAttachmentListeners) {
                            final OnAttachmentListeners attachment = (OnAttachmentListeners) record;
                            if (attachment.getIsDelete() || attachment.getAbsoluteFilePath() == null) {
                                continue;
                            }
                            final File file = new File(attachment.getAbsoluteFilePath());
                            final byte[] bytes = FileUtil.getBytesFromFile(file);
                            if (bytes.length == 0) {
                                final String proccessingAttachmentError = String.format(BaseApp.PROCESSING_ATTACHMENT_ERROR, attachment.getAbsoluteFilePath());
                                onError(IProgressStep.PACKAGE_CREATE, proccessingAttachmentError, tid);
                            } else {
                                utils.addFile(attachment.getId(), attachment.getId(), bytes);
                            }
                        }
                    }
                }
            }
            if (entity.to) {
                processingPackageTo(utils, entity.tableName, tid);
            }
            if (entity.from) {
                final TableQuery tableQuery = new TableQuery(entity.tableName, entity.select);
                final RPCItem rpcItem;
                if (entity.useCFunction) {
                    rpcItem = tableQuery.toRPCSelect(entity.params);
                } else {
                    rpcItem = tableQuery.toRPCQuery(MAX_COUNT_IN_QUERY, entity.filters);
                }
                utils.addFrom(rpcItem);
            }
        }
        return utils.generatePackage(tid);
    }

    @Override
    public void onProcessingPackage(final @NonNull PackageReadUtils utils, final @NonNull String tid) {
        /*
        Если хоть одна вставка была ошибочной, данные не добавлять
         */
        boolean success = true;
        try {
            for (final RPCResult result : utils.getToResult()) {
                if (result == null || result.action == null) {
                    continue;
                }
                final Entity entity = getEntity(result.action);
                if (entity == null || serverSidePackage == null) {
                    continue;
                }

                final PackageResult packageResult = serverSidePackage.to(daoSession, result, tid, entity.clearable);
                if (!packageResult.success) {
                    onError(IProgressStep.RESTORE, packageResult.message, tid);
                }
                if (success && !packageResult.success) {
                    success = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(IProgressStep.RESTORE, e, tid);
            success = false;
        }

        if (!success) {
            return;
        }

        try {
            if (serverSidePackage == null) {
                return;
            }
            final FullServerSidePackage fullServerSidePackage = (FullServerSidePackage) serverSidePackage;
            fullServerSidePackage.setFileBinary(utils.getFiles());

            for (final RPCResult result : utils.getFromResult()) {
                if (result.meta == null) {
                    onError(IProgressStep.PACKAGE_CREATE, BaseApp.EMPTY_META, tid);
                    continue;
                }
                if (!result.meta.success) {
                    onError(IProgressStep.PACKAGE_CREATE, StringUtil.defaultEmptyString(result.meta.msg), tid);
                    continue;
                }
                final String tableName = result.action;
                if (StringUtil.isEmpty(tableName)) {
                    continue;
                }
                final Entity entity = getEntity(tableName);
                if (entity == null) {
                    onError(IProgressStep.PACKAGE_CREATE, BaseApp.ENTITY_IS_NULL + StringUtil.defaultEmptyString(tableName), tid);
                    continue;
                }
                final PackageResult packageResult;
                if (tid.equals(fileTid) && useAttachments) {
                    packageResult = serverSidePackage.from(daoSession, result, tid, entity.to, true);
                } else {
                    packageResult = serverSidePackage.from(daoSession, result, tid, entity.to, false);
                }

                if (!packageResult.success) {
                    onError(IProgressStep.PACKAGE_CREATE, packageResult.message, tid);

                    if (packageResult.result instanceof Exception) {
                        Exception e = (Exception) packageResult.result;
                        onError(IProgressStep.PACKAGE_CREATE, StringUtil.defaultEmptyString(e.getMessage()), tid);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(IProgressStep.PACKAGE_CREATE, e, tid);
        }
    }
}
