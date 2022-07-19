package ru.mobnius.simple_core.utils;

/**
 * Стандарный интерефейс для передачи значения
 * прогресса выполнения асинхронной операции
 *
 * {@param progress} значение прогресса в процентах
 */
public interface OnProgressCallback {
    void onProgress(final int progress);
}
