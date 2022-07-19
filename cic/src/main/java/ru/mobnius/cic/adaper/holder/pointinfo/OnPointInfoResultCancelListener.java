package ru.mobnius.cic.adaper.holder.pointinfo;

import androidx.annotation.NonNull;
/**
 * Обработчик нажатия на кнопку отмены выполнения задания (включает в себя обратный вызов для обработки успешной отмены задания)
 */
public interface OnPointInfoResultCancelListener {
    void onResultCancel(final @NonNull String resultId,
                        final @NonNull OnPointInfoResultCancelledListener listener);
}
