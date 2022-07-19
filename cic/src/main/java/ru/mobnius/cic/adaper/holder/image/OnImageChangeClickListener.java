package ru.mobnius.cic.adaper.holder.image;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.ImageItem;
/**
 * Обработчик нажатия на иконку фотографии в акте
 */
public interface OnImageChangeClickListener {
    void onImageChangeClick(final @NonNull ImageItem item);
}
