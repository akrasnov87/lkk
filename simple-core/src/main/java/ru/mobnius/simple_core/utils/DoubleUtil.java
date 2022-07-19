package ru.mobnius.simple_core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DoubleUtil {
    public static final double MINUS = -1D;
    public static final double ZERO = 0D;
    @NonNull
    public static final String ZERO_STRING = "0.0";

    /**
     * Преобразование значение в строку
     *
     * @param value значение
     * @return строка
     */
    @NonNull
    public static String convertToText(final @Nullable Double value) {
        return value == null ? StringUtil.EMPTY : String.valueOf(value);
    }

    @Nullable
    public static Double getDoubleOrNull(final @Nullable String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double getDoubleOrZero(final @Nullable Double value) {
        if (value == null) {
            return ZERO;
        }
        return  value;
    }

    public static double getDoubleOrMinus(final @Nullable Double value) {
        if (value == null) {
            return MINUS;
        }
        return value;
    }

    /**
     * Null-safe преобразование объекта Double в строку
     *
     * @param value значение типа Double
     * @return строковое представление {@param value} либо "0.0"
     */
    @NonNull
    public static String getNonNullDoubleString(final @Nullable Double value) {
        if (value == null) {
            return ZERO_STRING;
        }
        try {
            final String stringValue = String.valueOf(value);
            if (stringValue.contains(StringUtil.COMMA)) {
                return stringValue.replace(StringUtil.COMMA, StringUtil.DOT);
            }
            return stringValue;
        } catch (Exception e) {
            e.printStackTrace();
            return ZERO_STRING;
        }
    }
}
