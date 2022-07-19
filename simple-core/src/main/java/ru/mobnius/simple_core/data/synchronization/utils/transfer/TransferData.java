package ru.mobnius.simple_core.data.synchronization.utils.transfer;

import androidx.annotation.NonNull;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Данные
 */
public class TransferData {
    /**
     * Текущая позиция
     */
    private final int position;
    /**
     * общий размер
     */
    private final int total;

    private TransferData(final int position, final int total) {
        this.position = position;
        this.total = total;
    }

    /**
     * Создани экземпляра объекта
     *
     * @param position текущай позиция
     * @param total    общий размер данных
     * @return Данные
     */
    @NonNull
    public static TransferData getInstance(final int position, final int total) {
        return new TransferData(position, total);
    }

    @NonNull
    public static TransferData getEmptyInstance() {
        return new TransferData(0, 0);
    }

    @NonNull
    @Override
    public String toString() {
        return StringUtil.getSize(position) + "/" + StringUtil.getSize(total);
    }
}
