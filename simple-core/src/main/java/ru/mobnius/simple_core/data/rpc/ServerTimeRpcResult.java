package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.Nullable;

public class ServerTimeRpcResult {
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
    public ServerTimeRecord result;

    @Nullable
    public String action;
    @Nullable
    public String method;
    public int tid;
    @Nullable
    public String type;
    @Nullable
    public String host;
    public int totalTime;

}
