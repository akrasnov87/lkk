package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Скорость передачи данных. Блоков chunk за время time
 */
public class TransferSpeed {
    /**
     * размер блока
     */
    private final long chunk;
    /**
     * время затраченное на обработку
     */
    private final long time;

    private TransferSpeed(final long chunk, final long time) {
        this.chunk = chunk;
        this.time = time;
    }

    @NonNull
    public static TransferSpeed getInstance(final long chunk, final long time) {
        long notZero = 1L;
        if (time != 0) {
            notZero = time;
        }
        return new TransferSpeed(chunk, notZero);
    }

    @NonNull
    public static TransferSpeed getEmptyInstance() {
        return new TransferSpeed(1L, 1L);
    }

    @NonNull
    @Override
    public String toString() {
        return StringUtil.getSize((1000 * chunk) / time) + "\\сек.";
    }
}
