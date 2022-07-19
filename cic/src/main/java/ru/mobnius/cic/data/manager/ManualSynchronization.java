package ru.mobnius.cic.data.manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.data.storage.AuditUtil;
import ru.mobnius.cic.data.storage.models.CausesDao;
import ru.mobnius.cic.data.storage.models.ResultHistoryDao;
import ru.mobnius.cic.data.storage.models.ResultStatusesDao;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.synchronization.Entity;
import ru.mobnius.simple_core.data.synchronization.EntityAttachment;
import ru.mobnius.simple_core.data.synchronization.EntityDictionary;
import ru.mobnius.simple_core.data.synchronization.FileTransferWebSocketSynchronization;
import ru.mobnius.simple_core.data.synchronization.IProgress;
import ru.mobnius.simple_core.data.synchronization.IProgressStep;
import ru.mobnius.cic.data.storage.models.AttachmentTypesDao;
import ru.mobnius.cic.data.storage.models.AttachmentsDao;
import ru.mobnius.cic.data.storage.models.DaoSession;
import ru.mobnius.cic.data.storage.models.PointsDao;
import ru.mobnius.cic.data.storage.models.ResultsDao;
import ru.mobnius.cic.data.storage.models.RouteHistoryDao;
import ru.mobnius.cic.data.storage.models.RouteStatusesDao;
import ru.mobnius.cic.data.storage.models.RoutesDao;
import ru.mobnius.simple_core.data.synchronization.utils.FullServerSidePackage;
import ru.mobnius.simple_core.utils.PackageReadUtils;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Ручная синхронизация данных
 */
public class ManualSynchronization extends FileTransferWebSocketSynchronization {

    @Nullable
    public String generalTid;
    @Nullable
    public String dictionaryTid;
    @Nullable
    private static ManualSynchronization manualSynchronization;

    @Nullable
    public static ManualSynchronization getInstance(final @NonNull String version, final boolean zip) {
        if (manualSynchronization == null) {
            if (DataManager.getInstance() == null) {
                return null;
            }
            manualSynchronization = new ManualSynchronization(version, DataManager.getInstance().daoSession, zip);
        }
        return manualSynchronization;
    }

    /**
     * конструктор
     *
     * @param session сессия для подключения к БД
     */
    protected ManualSynchronization(final @NonNull String version,
                                    final @NonNull DaoSession session,
                                    final boolean zip) {
        super(version, session, zip);
        oneOnlyMode = true;
        useAttachments = true;
        serverSidePackage = new FullServerSidePackage();
    }

    @Override
    protected void initEntities() {
        entityList.clear();
        generalTid = UUID.randomUUID().toString();
        dictionaryTid = UUID.randomUUID().toString();
        fileTid = UUID.randomUUID().toString();

        entityList.add(new EntityDictionary(RouteStatusesDao.TABLENAME, false, true).setTid(dictionaryTid).setParam(version).setUseCFunction());
        entityList.add(new EntityDictionary(ResultStatusesDao.TABLENAME, false, true).setTid(dictionaryTid).setParam(version).setUseCFunction());
        entityList.add(new EntityDictionary(AttachmentTypesDao.TABLENAME, false, true).setTid(dictionaryTid).setParam(version).setUseCFunction());
        entityList.add(new EntityDictionary(CausesDao.TABLENAME, false, true).setTid(dictionaryTid).setParam(version).setUseCFunction());

        entityList.add(Entity.createInstance(RouteHistoryDao.TABLENAME, true, true).setTid(generalTid).setParam(version).setUseCFunction());
        entityList.add(Entity.createInstance(RoutesDao.TABLENAME, false, true).setTid(generalTid).setParam(version).setUseCFunction());
        entityList.add(Entity.createInstance(ResultHistoryDao.TABLENAME, true, true).setTid(generalTid).setParam(version).setUseCFunction());
        entityList.add(Entity.createInstance(PointsDao.TABLENAME, true, true).setTid(generalTid).setParam(version).setUseCFunction());
        entityList.add(Entity.createInstance(ResultsDao.TABLENAME, true, true).setTid(generalTid).setParam(version).setUseCFunction());

        entityList.add(new EntityAttachment(AttachmentsDao.TABLENAME, true, true).setParam(version).setUseCFunction().setTid(fileTid));
    }

    @Override
    public void start(@NonNull IProgress progress) {
        super.start(progress);
        if (StringUtil.isEmpty(dictionaryTid) || StringUtil.isEmpty(generalTid)) {
            onError(IProgressStep.START, MobniusApplication.TID_IS_EMPTY, null);
            return;
        }
        onProgress(IProgressStep.START, MobniusApplication.DOWNLOADING_DICTIONARY_DATA);
        onProgress(IProgressStep.START, MobniusApplication.DOWNLOADING_GENERAL_DATA);

        try {
            final byte[] dictionaryBytes = generatePackage(dictionaryTid);
            sendBytes(dictionaryTid, dictionaryBytes);
            AuditUtil.writeAudit(AuditUtil.TRAFFIC_OUTPUT, MobniusApplication.DICTIONARY_SENT_SIZE + dictionaryBytes.length, version);
        } catch (Exception e) {
            onError(IProgressStep.START, BaseApp.PACKAGE_PROCESSING_ERROR + e, dictionaryTid);
        }
        try {
            final byte[] totalBytes = generatePackage(generalTid);
            sendBytes(generalTid, totalBytes, () -> {
                try {
                    if (StringUtil.isEmpty(fileTid)) {
                        onError(IProgressStep.START, BaseApp.FILE_TID_IS_EMPTY, null);
                        return;
                    }
                    final byte[] fileBytes = generatePackage(fileTid);
                    sendBytes(fileTid, fileBytes);
                    AuditUtil.writeAudit(AuditUtil.TRAFFIC_OUTPUT, MobniusApplication.FILES_SENT_SIZE + fileBytes.length, version);
                } catch (Exception e) {
                    onError(IProgressStep.START, BaseApp.PACKAGE_PROCESSING_ERROR + e, fileTid);
                }
            });
            AuditUtil.writeAudit(AuditUtil.TRAFFIC_OUTPUT, MobniusApplication.GENERAL_SENT_SIZE + totalBytes.length, version);
        } catch (Exception e) {
            onError(IProgressStep.START, BaseApp.PACKAGE_PROCESSING_ERROR + e, generalTid);
        }

    }

    @Override
    public void onProcessingPackage(@NonNull PackageReadUtils utils, @NonNull String tid) {
        super.onProcessingPackage(utils, tid);
        try {
            AuditUtil.writeAudit(AuditUtil.TRAFFIC_INPUT,
                    AuditUtil.getMessageByTid(tid, getTidMessageMap()) + utils.getLength(),
                    version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        manualSynchronization = null;
    }

    @NonNull
    private Map<String, String> getTidMessageMap() {
        Map<String, String > map = new HashMap<>();
        if (StringUtil.isNotEmpty(fileTid)) {
            map.put(fileTid, MobniusApplication.FILES_RECEIVED_SIZE);
        }
        if (StringUtil.isNotEmpty(dictionaryTid)) {
            map.put(dictionaryTid, MobniusApplication.DICTIONARY_RECEIVED_SIZE);
        }
        if (StringUtil.isNotEmpty(generalTid)) {
            map.put(generalTid, MobniusApplication.GENERAL_RECEIVED_SIZE);
        }
        return map;
    }

}
