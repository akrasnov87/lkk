package ru.mobnius.simple_core.utils;

import androidx.annotation.Nullable;

public class IntUtil {
    public static final int MINUS = -1;
    public static final int ZERO = 0;

    /**
     * Преобразование значение в строку
     *
     * @param value значение
     * @return строка
     */
    public static String toStringValue(Integer value) {
        return value == null ? StringUtil.EMPTY : String.valueOf(value);
    }

    /**
     * Метод преобразует объект в int если возможно
     * или же возвращает -1
     *
     * @param value объект для преобразования
     * @return значение объекта преобразованное в int или -1
     */
    public static int getIntOrMinus(final @Nullable Object value) {
        if (value == null) {
            return MINUS;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return MINUS;
        }
    }

    /**
     * Метод преобразует объект в int если возможно
     * или же возвращает 0
     *
     * @param value объект для преобразования
     * @return значение объекта преобразованное в int или 0
     */
    public static int getIntOrZero(final @Nullable Object value) {
        if (value == null) {
            return ZERO;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return ZERO;
        }
    }
}
