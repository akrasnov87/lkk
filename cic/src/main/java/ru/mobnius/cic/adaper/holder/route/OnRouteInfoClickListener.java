package ru.mobnius.cic.adaper.holder.route;

import androidx.annotation.NonNull;

import ru.mobnius.cic.ui.model.RouteItem;
/**
 * Обратный вызов для обработки нажатия на кнопку информации о маршруте
 */
public interface OnRouteInfoClickListener {
    void onRouteInfoClick(final @NonNull RouteItem routeItem);
}
