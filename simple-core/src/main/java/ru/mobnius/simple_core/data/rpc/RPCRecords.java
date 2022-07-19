package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

/**
 * результат с записями
 */
public class RPCRecords {
    /**
     * список записей
     */
    @Nullable
    public JsonObject[] records;

    /**
     * количество записей
     */
    public int total;
}
