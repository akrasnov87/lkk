package ru.mobnius.cic.adaper.holder.image;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.ImageItem;
/**
 * Обработчик нажатия на элемент фотографии в акте (для изменения типа фото или добавления комментария)
 */
public interface OnImageItemClickListener {
    void onImageClicked(final @NonNull ImageItem item);
}
