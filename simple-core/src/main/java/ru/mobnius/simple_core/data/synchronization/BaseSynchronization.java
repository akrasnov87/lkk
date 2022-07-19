package ru.mobnius.simple_core.data.synchronization;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.mobnius.simple_core.data.DbOperationType;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.packager.MetaPackage;
import ru.mobnius.simple_core.data.packager.MetaSize;
import ru.mobnius.simple_core.data.rpc.RPCItem;
import ru.mobnius.simple_core.data.storage.DbConst;
import ru.mobnius.simple_core.data.synchronization.utils.IServerSidePackage;
import ru.mobnius.simple_core.utils.PackageCreateUtils;
import ru.mobnius.simple_core.utils.PackageReadUtils;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.SyncUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Базовый абстрактный класс синхронизации
 */
public abstract class BaseSynchronization implements OnSynchronizationListeners {
    /**
     * Максимальное количество получаемых данных для одной сущности
     */
    public static final int MAX_COUNT_IN_QUERY = 100000;

    /**
     * объект подключения к БД
     */
    @NonNull
    public final AbstractDaoSession daoSession;

    /**
     * Список сущностей участвующих в синхронизации
     */
    @NonNull
    protected final ArrayList<Entity> entityList;

    @NonNull
    protected final String version;
    /**
     * Обработчик реузльтат синхронизации
     */
    @Nullable
    protected IProgress iProgress;

    /**
     * ОБработчик результата от сервера
     */
    @Nullable
    protected IServerSidePackage serverSidePackage;

    /**
     * обработка RPC по одной записи.
     * Устанавливается true если в результате запроса возникает ошибка и нужно определить какая запись в этом виновата.
     */
    public boolean oneOnlyMode = false;

    /**
     * Запущен ли процесс синхронизации
     */
    protected boolean isRunning = false;

    protected final boolean isZip;

    protected BaseSynchronization(final @NonNull String version,
                                  final @NonNull AbstractDaoSession daoSession,
                                  final boolean isZip) {
        this.version = version;
        this.daoSession = daoSession;
        this.isZip = isZip;
        entityList = new ArrayList<>();
    }

    /**
     * Запуск на выполение
     *
     * @param progress результат выполнения
     */
    @Override
    public void start(final @NonNull IProgress progress) {
        if (isRunning) {
            this.stop();
        } else {
            isRunning = true;
        }
        initEntities();
        this.iProgress = progress;
        SyncUtil.resetTid(this);

        onProgress(IProgressStep.START, BaseApp.STARTING_SYNC_PROCESS);
    }

    /**
     * Список сущностей
     *
     * @return возвращается список сущностей по которым разрешена отправка на сервер
     */
    @NonNull
    public List<Entity> getEntityToList() {
        final ArrayList<Entity> list = new ArrayList<>();
        for (final Entity entity : entityList) {
            if (entity.to) {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * список записей которые подверглись изменению
     *
     * @param tableName имя таблицы
     * @param tid       иднтификатор транзакции
     * @return возвращается массив данных
     */
    @NonNull
    public List<?> getRecords(final @NonNull String tableName, final @NonNull String tid) {
        return getRecords(tableName, tid, null);
    }

    /**
     * список записей которые подверглись изменению
     *
     * @param tableName     имя таблицы
     * @param tid           иднтификатор транзакции
     * @param operationType тип операции
     * @return возвращается массив данных
     */
    @NonNull
    protected List<?> getRecords(final @NonNull String tableName, final @NonNull String tid, final @Nullable String operationType) {
        List<?> resultList = new ArrayList<>();
        final AbstractDao<?, ?> abstractDao = getAbstractDao(tableName);
        if (abstractDao == null) {
            return resultList;
        }
        final QueryBuilder<?> queryBuilder = abstractDao.queryBuilder();
        if (StringUtil.isEmpty(tid)) {
            resultList = queryBuilder.list();
            if (resultList == null) {
                return new ArrayList<>();
            }
            return resultList;
        }
        Property isSyncProperty = null;
        Property tidProperty = null;
        Property objectOperationTypeProperty = null;
        for (final Property property : abstractDao.getProperties()) {
            if (StringUtil.equalsIgnoreCase(property.columnName, DbConst.IS_SYNCHRONIZATION)) {
                isSyncProperty = property;
                continue;
            }
            if (StringUtil.equalsIgnoreCase(property.columnName, DbConst.TID)) {
                tidProperty = property;
                continue;
            }
            if (StringUtil.equalsIgnoreCase(property.columnName, DbConst.OBJECT_OPERATION_TYPE)) {
                objectOperationTypeProperty = property;
            }
        }
        if (isSyncProperty == null || tidProperty == null || objectOperationTypeProperty == null) {
            return new ArrayList<>();
        }
        if (operationType != null) {
            resultList = queryBuilder.where(isSyncProperty.eq(0),
                    tidProperty.eq(tid),
                    objectOperationTypeProperty.eq(operationType)).list();
        } else {
            resultList = queryBuilder.where(isSyncProperty.eq(0),
                    tidProperty.eq(tid),
                    objectOperationTypeProperty.isNotNull(),
                    objectOperationTypeProperty.notEq(StringUtil.EMPTY)).list();
        }
        if (resultList == null) {
            return new ArrayList<>();
        }
        return resultList;
    }

    /**
     * Создание пакета с данными
     *
     * @param tid  идентификатор пакета
     * @return возвращается массив байтов
     */
    @NonNull
    protected abstract byte[] generatePackage(final @NonNull String tid) throws Exception;

    /**
     * Отправка данных на сервер
     *
     * @param tid   идентификатор транзакции
     * @param bytes массив байтов
     * @return результат отправки
     */
    protected abstract void sendBytes(final @NonNull String tid, final @NonNull byte[] bytes);


    /**
     * Отправка данных на сервер для файлов(фотографии и видео)
     *
     * @param tid                       идентификатор транзакции
     * @param bytes                     массив байтов
     * @param fileTransferStartCallback обратный вызов для гарантирования
     *                                  начала трансфера файлов в нужное время (после всех других данных)
     * @return результат отправки
     */
    protected abstract void sendBytes(final @NonNull String tid, final @NonNull byte[] bytes,
                                      final @NonNull FileTransferStartCallback fileTransferStartCallback);

    /**
     * Инициализация сущностей
     */
    protected abstract void initEntities();

    /**
     * Обработка пакета с результатом
     *
     * @param tids  список валидных идентификаторов пакета
     * @param bytes массив байтов
     */
    public void processingPackage(final @NonNull String[] tids, final @NonNull byte[] bytes) {
        final PackageReadUtils utils = new PackageReadUtils(bytes, isZip);
        MetaPackage metaPackage;
        try {
            metaPackage = utils.getMeta();
        } catch (Exception e) {
            e.printStackTrace();
            onError(IProgressStep.PACKAGE_CREATE, StringUtil.defaultEmptyString(e.getMessage()), null);
            utils.destroy();
            return;
        }
        if (metaPackage == null) {
            onError(IProgressStep.PACKAGE_CREATE, BaseApp.META_PACKAGE_CREATE_ERROR_MESSAGE, null);
            utils.destroy();
            return;
        }
        final String currentTid = metaPackage.id;
        boolean notContainsTid = true;
        for (final String tid : tids) {
            if (StringUtil.equalsIgnoreCase(tid, currentTid)) {
                notContainsTid = false;
                break;
            }
        }
        if (notContainsTid) {
            onError(IProgressStep.PACKAGE_CREATE, BaseApp.NO_TID_IN_TID_LIST, null);
            utils.destroy();
            return;
        }
        updateFinishedByEntity(currentTid);
        MetaSize metaSize;
        try {
            metaSize = utils.getMetaSize();
        } catch (Exception e) {
            e.printStackTrace();
            onError(IProgressStep.PACKAGE_CREATE, BaseApp.META_SIZE_CREATE_ERROR_MESSAGE, null);
            utils.destroy();
            return;
        }
        if (metaSize.status == MetaSize.CREATED) {
            onProcessingPackage(utils, currentTid);
        } else {
            onError(IProgressStep.PACKAGE_CREATE,
                    String.format(BaseApp.META_SIZE_STATUS_WRONG_MESSAGE, metaSize.status, MetaSize.CREATED), currentTid);
        }
        utils.destroy();
    }

    /**
     * Обработчка пакета для отправки данных
     *
     * @param utils     объект для формирования пакета
     * @param tableName имя сущности
     * @param tid       идентификатор транзакции
     */
    protected void processingPackageTo(final @NonNull PackageCreateUtils utils,
                                       final @NonNull String tableName,
                                       final @NonNull String tid) {
        final Object[] createRecords = getRecords(tableName, tid, DbOperationType.CREATED).toArray();
        final Object[] updateRecords = getRecords(tableName, tid, DbOperationType.UPDATED).toArray();
        final Object[] removeRecords = getRecords(tableName, tid, DbOperationType.REMOVED).toArray();
        AbstractDao<?, ?> abstractDao = getAbstractDao(tableName);
        if (abstractDao == null) {
            return;
        }
        if (createRecords.length == 0 && updateRecords.length == 0 && removeRecords.length == 0) {
            return;
        }
        final Entity entity = getEntity(tableName);
        if (entity == null) {
            return;
        }
        if (oneOnlyMode && !entity.many) {
            final Property pkProperty = abstractDao.getPkProperty();
            if (pkProperty == null || pkProperty.name == null) {
                return;
            }
            final String linkName = abstractDao.getPkProperty().name;
            processMasterRecords(utils, createRecords, tableName, tid, linkName);
            processMasterRecords(utils, updateRecords, tableName, tid, linkName);
            processMasterRecords(utils, removeRecords, tableName, tid, linkName);
        } else {
            processSupportRecords(utils, createRecords, tableName, tid, DbOperationType.CREATED);
            processSupportRecords(utils, updateRecords, tableName, tid, DbOperationType.UPDATED);
            processSupportRecords(utils, removeRecords, tableName, tid, DbOperationType.REMOVED);
        }
    }

    /**
     * обработкик пакетов
     *
     * @param utils объект для обработки
     * @param tid   идентификатор транзакции
     */
    protected abstract void onProcessingPackage(final @NonNull PackageReadUtils utils, final @NonNull String tid);

    /**
     * обработчик вывода статуса обработки
     *
     * @param step    шаг
     * @param message текст сообщения
     */
    protected void onProgress(final int step, final String message) {
        if (iProgress == null) {
            return;
        }
        iProgress.onProgress(this, step, message, null);
    }

    /**
     * обработчик ошибок
     *
     * @param step шаг
     * @param e    исключение
     * @param tid  идентификатор транзакции
     */
    public void onError(final int step, final @NonNull Exception e, final @Nullable String tid) {
        onError(step, StringUtil.defaultEmptyString(e.getMessage()), tid);
    }

    /**
     * обработчик ошибок
     *
     * @param step    шаг
     * @param message текст сообщения
     * @param tid     идентификатор транзакции
     */
    public void onError(final int step, final @NonNull String message,
                        final @Nullable String tid) {
        if (iProgress == null) {
            return;
        }
        iProgress.onError(this, step, message, tid);
    }

    /**
     * Текущая сущность
     *
     * @param tableName имя таблицы
     * @return Возвращается текущая сущность
     */
    @Nullable
    public Entity getEntity(final @NonNull String tableName) {
        for (final Entity entity : entityList) {
            if (StringUtil.equalsIgnoreCase(entity.tableName, tableName)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Возвращается список идентификатор пакетов
     *
     * @return массив идентификатор
     */
    @NonNull
    protected String[] getCollectionTid() {
        final String[] results = new String[entityList.size()];
        int i = 0;
        for (final Entity e : entityList) {
            results[i] = e.tid;
            i++;
        }
        return results;
    }

    /**
     * Список сущностей
     *
     * @param tid идентификатор транзакции
     * @return возвращается список сущностей с tid
     */
    @NonNull
    public Entity[] getEntities(final @NonNull String tid) {
        final ArrayList<Entity> results = new ArrayList<>(entityList.size());
        for (final Entity entity : entityList) {
            // обработка только элемента с указанным ключом
            if (StringUtil.equalsIgnoreCase(entity.tid, tid)) {
                results.add(entity);
            }
        }
        return results.toArray(new Entity[0]);
    }

    /**
     * Завершена ли обработка сущностей (пакетов)
     *
     * @return возвращается статус завершения
     */
    protected boolean isEntityFinished() {
        for (final Entity entity : entityList) {
            if (!entity.finished) {
                return false;
            }
        }
        return true;
    }

    /**
     * Обновление статуса finished у entity
     *
     * @param tid идентификатор пакета
     */
    protected void updateFinishedByEntity(final @NonNull String tid) {
        for (final Entity entity : entityList) {
            if (entity.tid.equals(tid)) {
                entity.setFinished();
            }
        }
    }

    /**
     * Принудительная остановка выполнения
     */
    @Override
    public void stop() {
        onProgress(IProgressStep.STOP, BaseApp.SYNC_COMPLETED);
        SyncUtil.resetTid(this);
        entityList.clear();
        isRunning = false;
        if (iProgress == null) {
            return;
        }
        iProgress.onStop(this);
        iProgress = null;
    }

    @Override
    public void cancel() {
        SyncUtil.resetTid(this);
        entityList.clear();
        iProgress = null;
        isRunning = false;
    }

    /**
     * Получение объекта таблицы по ее имени
     *
     * @param tableName имя таблицы
     * @return объект с данными таблицы
     */
    @Nullable
    private AbstractDao<?, ?> getAbstractDao(final @NonNull String tableName) {
        final Collection<AbstractDao<?, ?>> allDaos = daoSession.getAllDaos();
        for (final AbstractDao<?, ?> abstractDao : allDaos) {
            if (StringUtil.equalsIgnoreCase(abstractDao.getTablename(), tableName)) {
                return abstractDao;
            }
        }
        return null;
    }


    private void processMasterRecords(final @NonNull PackageCreateUtils utils,
                                      final @NonNull Object[] records,
                                      final @NonNull String tableName,
                                      final @NonNull String tid,
                                      final @NonNull String linkName) {
        if (records.length == 0) {
            return;
        }
        for (final Object o : records) {
            final Object linkValue = getLinkValue(o);
            if (linkValue == null) {
                continue;
            }
            final RPCItem rpc = RPCItem.addItem(tableName, o);
            utils.addTo(rpc);
            SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), linkName, linkValue);
        }
    }

    @Nullable
    private Object getLinkValue(final @Nullable Object o) {
        if (o == null) {
            return null;
        }
        try {
            final Field fieldId = o.getClass().getDeclaredField(DbConst.ID);
            fieldId.setAccessible(true);
            return fieldId.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processSupportRecords(final @NonNull PackageCreateUtils utils,
                                       final @NonNull Object[] records,
                                       final @NonNull String tableName,
                                       final @NonNull String tid,
                                       final @NonNull String dbOperationType) {
        if (records.length == 0) {
            return;
        }
        final RPCItem rpc = RPCItem.addItems(tableName, records);
        utils.addTo(rpc);
        SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), dbOperationType);

    }

    /**
     * Удаление объекта
     */
    public void destroy() {
        stop();
        iProgress = null;
    }

}
