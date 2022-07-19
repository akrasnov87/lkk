package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.Nullable;

/**
 * метаописание результата запроса
 */
public class RPCResultMeta {
    /**
     * статус выполнения, если true - то выполнен удачно
     */
    public boolean success;
    /**
     * текст сообщения, если запрос завершился с ошибкой: success = false
     */
    @Nullable
    public String msg;
}
