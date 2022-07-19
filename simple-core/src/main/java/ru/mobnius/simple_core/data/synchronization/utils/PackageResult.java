package ru.mobnius.simple_core.data.synchronization.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Результат выволнения операции
 */
public class PackageResult {
    /**
     * состояние выполнения
     */
    public boolean success;
    /**
     * тестовое сообщение
     */
    @NonNull
    public String message = StringUtil.EMPTY;
    /**
     * Результат
     */
    @Nullable
    public Object result;

    /**
     * Положительный результат
     *
     * @param result Результат
     * @return объект
     */
    public static PackageResult success(final @Nullable Object result) {
        PackageResult packageResult = new PackageResult();
        packageResult.success = true;
        packageResult.result = result;
        return packageResult;
    }

    /**
     * Отрицательный результат
     *
     * @param message Текстовое сообщение
     * @param e       исключение
     * @return результат
     */
    public static PackageResult fail(final @NonNull String message, final @Nullable Exception e) {
        PackageResult packageResult = new PackageResult();
        packageResult.success = false;
        packageResult.message = message;
        packageResult.result = e;
        return packageResult;
    }
}
