package ru.mobnius.simple_core.data.synchronization.utils;

import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.packager.FileBinary;
import ru.mobnius.simple_core.data.rpc.RPCResult;
import ru.mobnius.simple_core.data.rpc.RpcConstants;
import ru.mobnius.simple_core.data.storage.DbConst;
import ru.mobnius.simple_core.utils.SqlStatementInsertFromJSONObject;
import ru.mobnius.simple_core.utils.SqlUpdateFromJSONObject;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * абстрактный обработчик результат от сервера
 */
public abstract class ServerSidePackage implements IServerSidePackage {
    protected boolean deleteRecordBeforeAppend = false;
    @Nullable
    protected FileBinary[] fileBinaries;

    /**
     * Устанавливает параметр удаление записей при добавлении информации в БД
     *
     * @param value значение
     */
    public void setDeleteRecordBeforeAppend(final boolean value) {
        deleteRecordBeforeAppend = value;
    }

    /**
     * Удалять записи при добавлении информации в БД
     *
     * @return true - все записи из таблицы будут удалены
     */
    public boolean getDeleteRecordBeforeAppend() {
        return deleteRecordBeforeAppend;
    }

    /**
     * Добавление бинарного блока
     *
     * @param fileBinary бинарный блок
     */
    public void setFileBinary(final @NonNull FileBinary[] fileBinary) {
        this.fileBinaries = fileBinary;
    }

    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     *
     * @param session    сессия
     * @param rpcResult  результат RPC
     * @param packageTid идентификатор пакета
     * @param clearable  требуетяс очистка после успешной передачи
     * @return результат
     */
    @NonNull
    @Override
    public PackageResult to(final @NonNull AbstractDaoSession session,
                            final @NonNull RPCResult rpcResult,
                            final @NonNull String packageTid,
                            final @NonNull Boolean clearable) {
        if (rpcResult.meta == null) {
            return PackageResult.fail(BaseApp.NO_META_DATA_FROM_SERVER, null);
        }
        if (!rpcResult.meta.success) {
            return PackageResult.fail(StringUtil.defaultEmptyString(rpcResult.meta.msg), null);
        }
        if (clearable) {
            // тут все хорошо, нужно удалить все записи c tid
            final Database db = session.getDatabase();
            final Object[] params = new Object[1];
            params[0] = packageTid;

            db.execSQL(DbConst.DELETE_FROM + rpcResult.action + DbConst.WHERE_TID_EQUAL, params);
            return PackageResult.success(null);
        } else {
            return to(session, rpcResult, packageTid);
        }
    }

    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     *
     * @param session    сессия
     * @param rpcResult  результат RPC
     * @param packageTid идентификатор пакета
     * @return результат
     */
    @NonNull
    @Override
    public PackageResult to(final @NonNull AbstractDaoSession session,
                            final @NonNull RPCResult rpcResult,
                            final @NonNull String packageTid) {
        if (rpcResult.meta == null) {
            return PackageResult.fail(BaseApp.NO_META_DATA_FROM_SERVER, null);
        }
        final Database db = session.getDatabase();
        if (db == null) {
            return PackageResult.fail(BaseApp.ERROR_NO_DB_CONNECTION + rpcResult.meta.msg, null);
        }
        // если все хорошо обновляем запись в локальной БД
        String sqlQuery = StringUtil.EMPTY;
        Object[] values;
        try {
            if (!rpcResult.meta.success) {
                sqlQuery = String.format(DbConst.UPDATE_NOT_SUCCESS, rpcResult.action);
                values = new Object[5];
                values[0] = false;
                values[1] = null;
                values[2] = null;
                values[3] = packageTid;
                values[4] = String.valueOf(rpcResult.tid);

                db.execSQL(sqlQuery, values);
                return PackageResult.fail(BaseApp.ERROR_PROCESSING_BLOCK_ON_SERVER + rpcResult.meta.msg, null);
            } else {
                sqlQuery = String.format(DbConst.UPDATE_SUCCESS, rpcResult.action);
                values = new Object[6];
                values[0] = true;
                values[1] = null;
                values[2] = null;
                values[3] = null;
                values[4] = packageTid;
                values[5] = String.valueOf(rpcResult.tid);

                db.execSQL(sqlQuery, values);
                return PackageResult.success(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return PackageResult.fail(BaseApp.ERROR_UPDATE_POSITIVE_RESULT_IN_DB + sqlQuery + new Gson().toJson(rpcResult), e);
        }
    }

    /**
     * обработка блока from
     * результат обработки информации переданной в блоке from
     *
     * @param session           сессия
     * @param rpcResult         результат RPC
     * @param packageTid        идентификатор пакета
     * @param isRequestToServer может ли объект делать запрос на сервер.
     * @return результат
     */
    @NonNull
    public PackageResult from(final @NonNull AbstractDaoSession session,
                              final @NonNull RPCResult rpcResult,
                              final @NonNull String packageTid,
                              final boolean isRequestToServer) {
        return from(session, rpcResult, packageTid, isRequestToServer, false);
    }

    /**
     * обработка блока from
     * результат обработки информации переданной в блоке from
     *
     * @param session           сессия
     * @param rpcResult         результат RPC
     * @param packageTid        идентификатор пакета
     * @param isRequestToServer может ли объект делать запрос на сервер.
     * @param attachmentUse     применяется обработка вложений
     * @return результат
     */
    @NonNull
    @Override
    public PackageResult from(final @NonNull AbstractDaoSession session,
                              final @NonNull RPCResult rpcResult,
                              final @NonNull String packageTid,
                              final boolean isRequestToServer,
                              final boolean attachmentUse) {
        if (rpcResult.meta == null
                || rpcResult.method == null
                || rpcResult.result == null
                || rpcResult.result.records == null) {
            return PackageResult.fail(BaseApp.NO_META_DATA_FROM_SERVER, null);
        }
        if (!rpcResult.meta.success) {
            return PackageResult.fail(BaseApp.ERROR_PROCESSING_BLOCK_ON_SERVER + rpcResult.meta.msg, null);
        }
        final Database db = session.getDatabase();
        if (db == null) {
            return PackageResult.fail(BaseApp.ERROR_NO_DB_CONNECTION + rpcResult.meta.msg, null);
        }
        AbstractDao<?, ?> abstractDao = null;
        final String tableName = rpcResult.action;
        if (StringUtil.isEmpty(tableName)) {
            return PackageResult.fail(BaseApp.EMPTY_TABLE_NAME, null);
        }
        for (final AbstractDao<?, ?> ad : session.getAllDaos()) {
            if (ad.getTablename().equals(tableName)) {
                abstractDao = ad;
                break;
            }
        }
        if (abstractDao == null) {
            return PackageResult.fail(BaseApp.TABLE_NAME_NOT_FOUND + tableName,
                    new NullPointerException(BaseApp.ABSTRACT_DAO_IS_NULL));
        }

        if (StringUtil.notEquals(rpcResult.method, RpcConstants.QUERY)
                && StringUtil.notEquals(rpcResult.method, RpcConstants.SELECT)) {
            return PackageResult.fail(String.format(BaseApp.QUERY_METHOD_MISSMATCH, tableName, rpcResult.method), null);
        }
        db.beginTransaction();

        if (getDeleteRecordBeforeAppend() && rpcResult.code != RPCResult.PERMATENT) {
            db.execSQL(DbConst.DELETE_FROM + tableName);
            abstractDao.detachAll();
        }
        if (rpcResult.result.records.length == 0) {
            db.setTransactionSuccessful();
            db.endTransaction();
            return PackageResult.success(null);
        }
        final JsonObject firstObject = rpcResult.result.records[0];
        if (firstObject.has(RpcConstants.ERROR)) {
            String error = StringUtil.ERROR;
            try {
                error = firstObject.get(RpcConstants.ERROR).getAsString();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
            db.endTransaction();
            return PackageResult.fail(BaseApp.TABLE_INSERT_ERROR + tableName + error,
                    new Exception(error));
        }
        final SqlStatementInsertFromJSONObject sqlInsert =
                new SqlStatementInsertFromJSONObject(firstObject, tableName, isRequestToServer, abstractDao);
        try {
            for (final JsonObject object : rpcResult.result.records) {
                try {
                    sqlInsert.bind(object);
                } catch (SQLiteConstraintException e) {
                    String pkColumnName = StringUtil.EMPTY;
                    for (final AbstractDao<?, ?> a : session.getAllDaos()) {
                        if (StringUtil.equalsIgnoreCase(a.getTablename(), tableName)) {
                            pkColumnName = a.getPkProperty().columnName;
                            break;
                        }
                    }
                    if (StringUtil.isEmpty(pkColumnName)) {
                        return PackageResult.fail(BaseApp.TABLE_INSERT_ERROR + tableName + e.getMessage(),
                                new Exception(String.format(BaseApp.CAN_NOT_FIND_TABLE_PRIMARY_KEY, tableName)));
                    } else {
                        // тут обновление будет только у тех записей у которых не было изменений.
                        final SqlUpdateFromJSONObject sqlUpdate =
                                new SqlUpdateFromJSONObject(firstObject, tableName, pkColumnName, abstractDao);
                        db.execSQL(sqlUpdate.convertToQuery(isRequestToServer), sqlUpdate.getValues(object, isRequestToServer));
                    }
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            return PackageResult.success(null);
        } catch (Exception e) {
            db.endTransaction();
            return PackageResult.fail(BaseApp.TABLE_INSERT_ERROR + tableName + e.getMessage(), e);
        }
    }
}
