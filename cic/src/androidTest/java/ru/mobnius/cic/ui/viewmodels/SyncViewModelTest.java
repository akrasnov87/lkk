package ru.mobnius.cic.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.models.RouteHistory;
import ru.mobnius.cic.data.storage.models.RouteStatuses;
import ru.mobnius.cic.data.storage.models.RouteStatusesDao;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.cic.ui.model.SyncLogItem;
import ru.mobnius.cic.ui.model.SyncPercentageProgress;
import ru.mobnius.cic.ui.model.SyncProgressItem;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.data.socket.SocketManager;
import ru.mobnius.simple_core.data.synchronization.utils.transfer.TransferResult;

public class SyncViewModelTest extends BaseModelTest {
    private SyncViewModel viewModel;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void before() {
        viewModel = new SyncViewModel();
    }

    @Test
    public void init() {
        viewModel.init("1.1.1.1");
        assertNotNull(viewModel.socketConnectionData);
        assertNotNull(SocketManager.getInstance());
        assertNotNull(viewModel.syncListener);
        assertNotNull(viewModel.progressLiveData);
        assertNotNull(viewModel.logLiveData);
        viewModel.progressLiveData = null;
    }

    @Test
    public void start() throws InterruptedException {
        viewModel.init("1.1.1.1");
        viewModel.start("1.1.1.1", false);
        assertEquals(true, viewModel.circleProgressData.getValue());
        assertEquals(true, viewModel.startStopLoadingData.getValue());
        Thread.sleep(2000);
        assert viewModel.progressLiveData != null;
        assertNotNull(viewModel.progressLiveData.getValue());
        viewModel.progressLiveData = null;
    }

    @Test
    public void getProgressItems() {
        assertNotNull(viewModel.getProgressItems());
    }

    @Test
    public void getLogItems() {
        assertNotNull(viewModel.getLogItems());
    }

    @Test
    public void clearProgressItems() {
        viewModel.init("1.1.1.1");
        viewModel.progressItems.add(new SyncProgressItem("tid", 1, 2, 3, "name", "", ""));
        viewModel.clearProgressItems();
        assert viewModel.progressLiveData != null;
        assertEquals(viewModel.progressItems.size(), 0);
        assertEquals(Objects.requireNonNull(viewModel.progressLiveData.getValue()).errorMessage, "");
    }

    @Test
    public void clearLogItems() {
        viewModel.init("1.1.1.1");
        viewModel.logItems.add(new SyncLogItem("", false));
        viewModel.clearLogItems();
        assertEquals(viewModel.logItems.size(), 0);
        assert viewModel.logLiveData != null;
        assertEquals(viewModel.logLiveData.getValue(), viewModel.logItems);
    }

    @Test
    public void addStopLog() {
        viewModel.init("1.1.1.1");
        viewModel.addStopLog("stop");
        assert viewModel.logLiveData != null;
        final List<SyncLogItem> list = viewModel.logLiveData.getValue();
        assert list != null;
        final SyncLogItem syncLogItem = list.get(list.size() - 1);
        assertEquals(syncLogItem.c_message, "stop");
        assertFalse(syncLogItem.b_error);
    }

    @Test
    public void getRoutesReceived() throws InterruptedException, IllegalArgumentException {
        assert DataManager.getInstance() != null;
        final List<RouteStatuses> routeStatuses = DataManager.getInstance().daoSession.getRouteStatusesDao().
                queryBuilder().where(RouteStatusesDao.Properties.C_const.eq(RouteItem.ROUTE_STATUS_RECEIVED)).list();
        if (routeStatuses == null || routeStatuses.size() == 0) {
           throw new IllegalArgumentException("RouteStatus does not exist");
        }
        viewModel.getRoutesReceived();
        final long statusId = routeStatuses.get(0).id;
        Thread.sleep(1000);
        final RouteHistory routeHistory = DataManager.getInstance().daoSession.getRouteHistoryDao().loadAll().get(0);
        assertEquals(routeHistory.fn_status, statusId);
    }
}