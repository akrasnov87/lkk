package ru.mobnius.cic.adaper.holder.phototypes;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.ImageType;
/**
 * Обработчик нажатия на тип фотографии в диалоговом окне выбора типов фотографии
 */
public interface OnImageTypeSelectedListener {
    void onPhotoTypeSelected(final @NonNull ImageType imageType);
}
