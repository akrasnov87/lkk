package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

/**
 * Результат RPC вызова
 */
public class RPCResult {

    public static final int APPLICATION_ERROR = 600;
    public static final int PERMATENT = 301;

    /**
     * код результата
     */
    public int code;

    /**
     * метаописание результат запроса
     */
    @Nullable
    public RPCResultMeta meta;
    @Nullable
    public String action;
    @Nullable
    public String method;
    public int tid;
    @Nullable
    public String type;
    @Nullable
    public String host;
    @Nullable
    public RPCRecords result;

    /**
     * создание экземпляра
     *
     * @param requestResult результат запроса
     * @return обработанный объект
     */
    @NonNull
    public static RPCResult[] createInstanceByGson(final @NonNull String requestResult) {
        RPCResult[] result;
        try {
            if (requestResult.indexOf(RpcConstants.SQUARE_BRACKETS) == 0) {
                result = new Gson().fromJson(requestResult, RPCResult[].class);
            } else {
                result = new RPCResult[1];
                result[0] = new Gson().fromJson(requestResult, RPCResult.class);
            }
            return result;
        } catch (Exception e) {
            result = new RPCResult[1];
            result[0] = new RPCResult();
            result[0].code = APPLICATION_ERROR;
            result[0].meta = new RPCResultMeta();
            result[0].meta.success = false;
            result[0].meta.msg = requestResult;
            return result;
        }
    }

    /**
     * Результат обработки пакета
     *
     * @return true - пакет обработан без ошибок
     */
    public boolean isSuccess() {
        if (meta == null) {
            return false;
        }
        return meta.success;
    }
}
