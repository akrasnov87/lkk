package ru.mobnius.simple_core.data.synchronization.utils;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.AbstractDaoSession;

import ru.mobnius.simple_core.data.rpc.RPCResult;


/**
 * интерфейс для обработки пакетов принятых от сервера
 */
public interface IServerSidePackage {
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
    PackageResult to(final @NonNull AbstractDaoSession session,
                     final @NonNull RPCResult rpcResult,
                     final @NonNull String packageTid);

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
    PackageResult to(final @NonNull AbstractDaoSession session,
                     final @NonNull RPCResult rpcResult,
                     final @NonNull String packageTid,
                     final @NonNull Boolean clearable);

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
    PackageResult from(final @NonNull AbstractDaoSession session,
                       final @NonNull RPCResult rpcResult,
                       final @NonNull String packageTid,
                       final boolean isRequestToServer,
                       final boolean attachmentUse);
}
