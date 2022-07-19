package ru.mobnius.cic.adaper.holder.point;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.PointItem;
/**
 * Обработчик нажатия на кнопку информации об элементе маршрута
 */
public interface OnPointInfoClickListener {
    void onPointInfoClick(final @NonNull PointItem pointItem, final int position);
}
