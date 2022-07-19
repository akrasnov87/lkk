package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.utils.JsonUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * результат обработки ответа
 */
public class TransferResult {
    /**
     * метаинформация
     */
    @Nullable
    public Meta meta;

    /**
     * данные
     */
    @NonNull
    public final Data data;

    /**
     * результат
     */
    public byte[] result;

    /**
     * статус код
     */
    public int code;

    /**
     * идентияикатор транзакции
     */
    public String tid;


    public TransferResult(final @NonNull Data data) {
        this.data = data;
    }

    public static class Meta {
        /**
         * Статус завершенности
         */
        public boolean processed;
        /**
         * индикатор начала
         */
        public int start;
        /**
         * длина данных
         */
        public int totalLength;
    }

    public static class Data {
        /**
         * Результат выполнения
         */
        public boolean success;
        /**
         * сообщение результата выполнения
         */
        @NonNull
        public String msg;

        public Data(final @NonNull String msg, final boolean success) {
            this.msg = msg;
            this.success = success;
        }
    }

    /**
     * Создание результата сообщений об ошибке
     *
     * @param message текст сообщения
     * @return результат отправки
     */
    public static TransferResult error(final @NonNull String message) {
        final Data d = new Data(message, false);
        return new TransferResult(d);
    }

    /**
     * Чтение информации из входных данным
     *
     * @param object входной объект
     * @return результат передачи
     */
    public static TransferResult readResult(final @NonNull JSONObject object) {
        final String tid = JsonUtil.getNullableString(object, JsonUtil.TID_JSON_KEY);
        if (StringUtil.isEmpty(tid)) {
            return TransferResult.error(BaseApp.ERROR_READING_JSON + object);
        }

        JSONObject jsonData = JsonUtil.getNullableJsonObject(object, JsonUtil.DATA_JSON_KEY);
        if (jsonData == null) {
            return TransferResult.error(BaseApp.ERROR_READING_JSON + JsonUtil.DATA_JSON_KEY + object);
        }
        final String msg = JsonUtil.getNonNullString(jsonData, JsonUtil.MSG_JSON_KEY);
        final boolean success = JsonUtil.getBooleanFromJSONObject(jsonData, JsonUtil.SUCCESS_JSON_KEY, false);
        final Data d = new Data(msg, success);
        final TransferResult result = new TransferResult(d);
        try {
            result.result = (byte[]) object.get(JsonUtil.RESULT_JSON_KEY);
        } catch (ClassCastException | JSONException e) {
            e.printStackTrace();
            return TransferResult.error(BaseApp.ERROR_READING_JSON + StringUtil.defaultEmptyString(e.getMessage()));
        }
        result.tid = tid;
        result.code = JsonUtil.getIntFromJSONObject(object, JsonUtil.CODE_JSON_KEY, 0);

        final JSONObject jsonMeta = JsonUtil.getNullableJsonObject(object, JsonUtil.META_JSON_KEY);
        if (jsonMeta == null) {
            return TransferResult.error(BaseApp.ERROR_READING_JSON + JsonUtil.META_JSON_KEY + object);
        }
        final Meta m = new Meta();
        m.processed = JsonUtil.getBooleanFromJSONObject(jsonMeta, JsonUtil.PROCESSED_JSON_KEY, false);
        m.start = JsonUtil.getIntFromJSONObject(jsonMeta, JsonUtil.START_JSON_KEY, 0);
        m.totalLength = JsonUtil.getIntFromJSONObject(jsonMeta, JsonUtil.TOTAL_LENGTH_JSON_KEY, 0);
        result.meta = m;

        return result;

    }
}
