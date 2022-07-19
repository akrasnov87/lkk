package ru.mobnius.simple_core.data.synchronization.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Чтение статусной информации у сокета
 */
public class SocketStatusReader {

    /**
     * Наименование статуса
     */
    @NonNull
    public final String name;

    /**
     * Параметры переданные со статусом
     */
    @NonNull
    public final String[] params;

    private SocketStatusReader(final @NonNull String inputString) {
        // отсутвие символов исключено, так как их наличие проверяется перед вызовом конструктора
        final int i = inputString.indexOf("]");
        name = inputString.substring(1, i);
        final String paramsStr = inputString.substring(i + 1);
        params = paramsStr.split(";");
    }

    /**
     * Создание объекта
     *
     * @param status статусная строка
     * @return объект
     */
    @Nullable
    public static SocketStatusReader getInstance(String status) {
        if (status.matches("\\[\\w+]([\\S|\\s]+;?)+")) {
            return new SocketStatusReader(status);
        } else {
            return null;
        }
    }
}
