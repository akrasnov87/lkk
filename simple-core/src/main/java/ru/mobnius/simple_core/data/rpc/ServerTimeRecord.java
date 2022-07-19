package ru.mobnius.simple_core.data.rpc;

import androidx.annotation.Nullable;

public class ServerTimeRecord {
    /**
     * список записей
     */
    @Nullable
    public DateRecord[] records;

    /**
     * количество записей
     */
    public int total;

}

