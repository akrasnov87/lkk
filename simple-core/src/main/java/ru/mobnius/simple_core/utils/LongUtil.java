package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class LongUtil {
    public static final long MINUS = -1L;
    public static final long ZERO = 0L;

    /**
     * Преобразование Long в строку
     *
     * @param value значение
     * @return пустую строку либо long в виде строки
     */
    @NonNull
    public static String toStringValue(final @Nullable Long value) {
        return value == null ? StringUtil.EMPTY : String.valueOf(value);
    }

    /**
     * Метод преобразует объект в long если возможно
     * или же возвращает -1L
     *
     * @param value объект для преобразования
     * @return значение объекта преобразованное в long или -1L
     */
    public static long getLongOrMinus(final @Nullable Object value) {
        if (value == null) {
            return MINUS;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return MINUS;
        }
    }

    /**
     * Метод преобразует объект в long если возможно
     * или же возвращает 0L
     *
     * @param value объект для преобразования
     * @return значение объекта преобразованное в long или 0L
     */
    public static long getLongOrZero(final @Nullable Object value) {
        if (getLongOrMinus(value) < 0) {
            return ZERO;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ZERO;
        }
    }

    /**
     * Метод преобразует объект в long если возможно
     * или же возвращает null
     *
     * @param value объект для преобразования
     * @return значение объекта преобразованное в long или null
     */
    @Nullable
    public static Long getLongOrNull(final @Nullable Object value) {
        if (getLongOrMinus(value) < 0) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
