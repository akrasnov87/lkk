package ru.mobnius.cic.adaper.holder.point;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.PointItem;
/**
 * Обработчик нажатия на элемент маршрута
 */
public interface OnPointItemClickListener {
    void onPointItemClick(final @NonNull PointItem pointItem, final int position);
}
