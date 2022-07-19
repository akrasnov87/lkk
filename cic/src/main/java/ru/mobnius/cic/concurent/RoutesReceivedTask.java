package ru.mobnius.cic.concurent;

import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.models.Routes;
import ru.mobnius.cic.ui.model.RouteItem;

/**
 * Проверка доступных маршрутов и наличие у них статуса 'Принят',
 * если статуса нет то он проставляется
 * в дополнительном потоке
 */
public class RoutesReceivedTask implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        if (DataManager.getInstance() == null) {
            return null;
        }
        final List<Routes> routesList = DataManager.getInstance().daoSession.getRoutesDao().loadAll();
        for (final Routes route : routesList) {
            DataManager.getInstance().assignStatusIfNotAlreadyAssigned(route.id, RouteItem.ROUTE_STATUS_RECEIVED);
        }
        return null;
    }

}
