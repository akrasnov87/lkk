package ru.mobnius.cic.adaper.holder.route;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.RouteItem;
/**
 * Обратный вызов для обработки нажатия на элемент маршрута
 */
public interface OnRouteItemClickListener {
    void onRouteItemClick(final @NonNull RouteItem routeItem);
}
