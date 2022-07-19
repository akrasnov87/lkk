package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import ru.mobnius.simple_core.data.storage.DbConst;
import ru.mobnius.simple_core.data.synchronization.BaseSynchronization;
import ru.mobnius.simple_core.data.synchronization.Entity;
import ru.mobnius.simple_core.data.synchronization.IProgressStep;


public class SyncUtil {
    /**
     * Объект для сброса параметров транзакции
     */
    @NonNull
    public static Object[] getResetTidParams() {
        Object[] params = new Object[2];
        params[0] = null;
        params[1] = null;
        return params;
    }

    /**
     * Запрос на сброса транзакции
     *
     * @param tableName имя таблицы
     * @return sql-запрос
     */
    @NonNull
    private static String getResetTidSqlQuery(final @NonNull String tableName) {
        return "update " + tableName + " set " + DbConst.TID + " = ?, " + DbConst.BLOCK_TID + " = ?";
    }

    /**
     * Сбрас идентификаторов транзакций у таблиц по которым разрешено отправлять данные
     *
     * @return false если информация не была сброшена
     */
    public static void resetTid(final @NonNull BaseSynchronization baseSynchronization) {
        final AbstractDaoSession daoSession = baseSynchronization.daoSession;
        final Database db = daoSession.getDatabase();
        db.beginTransaction();
        try {
            final Object[] params = getResetTidParams();
            for (final Entity entity : baseSynchronization.getEntityToList()) {
                db.execSQL(getResetTidSqlQuery(entity.tableName), params);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            baseSynchronization.onError(IProgressStep.NONE, e, null);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Обновление идентификатора транзакции для записи
     *
     * @param tableName имя таблицы
     * @param tid       идентификатор транзакции
     * @return возвращается результат обработки
     */
    public static boolean updateTid(final @NonNull BaseSynchronization baseSynchronization,
                                    final @NonNull String tableName, final @NonNull String tid) {
        boolean result = false;
        final AbstractDaoSession daoSession = baseSynchronization.daoSession;
        final Database db = daoSession.getDatabase();
        try {
            final Object[] params = new Object[2];
            params[0] = tid;
            params[1] = "";
            db.execSQL("update " + tableName + " set " + DbConst.TID + " = ? where " + DbConst.TID + " is null OR " + DbConst.TID + " = ?", params);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            baseSynchronization.onError(IProgressStep.START, e, tid);
        }
        return result;
    }

    /**
     * Обновление идентификатора блока для записи
     *
     * @param tableName имя таблицы
     * @param tid       идентификатор транзакции
     * @param blockTid  идентификатор транзакции
     * @param linkName  имя первичного ключа
     * @param linkValue значение первичного ключа
     */
    public static void updateBlockTid(final @NonNull BaseSynchronization baseSynchronization, final @NonNull String tableName,
                                      final @NonNull String tid, final @NonNull String blockTid, final @NonNull String linkName, final @NonNull Object linkValue) {

        final AbstractDaoSession daoSession = baseSynchronization.daoSession;
        final Database db = daoSession.getDatabase();
        try {
            final Object[] params = new Object[2];
            params[0] = blockTid;
            params[1] = linkValue;
            db.execSQL("update " + tableName + " set " + DbConst.BLOCK_TID + " = ? where " + linkName + " = ?", params);
        } catch (Exception e) {
            e.printStackTrace();
            baseSynchronization.onError(IProgressStep.START, e, tid);
        }
    }

    /**
     * Обновление идентификатора блока для записи
     *
     * @param tableName     имя таблицы
     * @param tid           идентификатор транзакции
     * @param blockTid      идентификатор транзакции
     * @param operationType тип операции
     */
    public static void updateBlockTid(final @NonNull BaseSynchronization baseSynchronization, final @NonNull String tableName,
                                      final @NonNull String tid, final @NonNull String blockTid, final @NonNull String operationType) {
        final AbstractDaoSession daoSession = baseSynchronization.daoSession;
        final Database db = daoSession.getDatabase();
        try {
            final Object[] params = new Object[3];
            params[0] = blockTid;
            params[1] = tid;
            params[2] = operationType;
            db.execSQL("update " + tableName + " set " + DbConst.BLOCK_TID + " = ? where " + DbConst.TID + " = ? AND " + DbConst.OBJECT_OPERATION_TYPE + " = ?", params);
        } catch (Exception e) {
            e.printStackTrace();
            baseSynchronization.onError(IProgressStep.START, e, tid);
        }
    }

    /**
     * Обновление идентификатора блока для записи
     *
     * @param tableName имя таблицы
     * @param tid       идентификатор транзакции
     */
    public static void updateBlockTid(final @NonNull BaseSynchronization baseSynchronization,
                                      final @NonNull String tableName, final @NonNull String tid) {
        final AbstractDaoSession daoSession = baseSynchronization.daoSession;
        final Database db = daoSession.getDatabase();
        try {
            final Object[] params = new Object[3];
            params[0] = null;
            params[1] = null;
            params[2] = tid;
            db.execSQL("update " + tableName + " set " + DbConst.BLOCK_TID + " = ?, " + DbConst.OBJECT_OPERATION_TYPE + " = ? where " + DbConst.TID + " = ? ", params);
        } catch (Exception e) {
            e.printStackTrace();
            baseSynchronization.onError(IProgressStep.STOP, e, tid);
        }
    }

}