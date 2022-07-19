package ru.mobnius.cic.adaper.holder.url;

import androidx.annotation.NonNull;

/**
 * Обработчик выбора нового url
 */
public interface OnSelectNewUrl {
    void onNewUrlSelected(final @NonNull String serverUrl, final @NonNull String enviroment);
}
