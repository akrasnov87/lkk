package ru.mobnius.cic.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.CONSTS;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.concurent.LoadRoutesResult;

public class RouteViewModelTest extends BaseModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private final RouteViewModel viewModel = new RouteViewModel();


    @Test
    public void refreshRoutes() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<LoadRoutesResult> liveData = viewModel.refreshRoutes();
        liveData.observeForever(userProfile -> latch.countDown());
        if (!latch.await(1, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        assert DataManager.getInstance() != null;
        assertEquals(Objects.requireNonNull(liveData.getValue()).getAllRouteItems().size(), 1);
    }

    @Test
    public void searchAsync() throws InterruptedException {
        viewModel.query = CONSTS.ROUTE_NAME;
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<LoadRoutesResult> liveData = viewModel.searchAsync();
        liveData.observeForever(userProfile -> latch.countDown());
        if (!latch.await(1, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        assertEquals(Objects.requireNonNull(liveData.getValue()).getAllRouteItems().size(), 1);
        viewModel.query = "qwerty";
        final CountDownLatch latch1 = new CountDownLatch(1);
        final MutableLiveData<LoadRoutesResult> liveData1 = viewModel.searchAsync();
        Thread.sleep(1000);
        liveData1.observeForever(result -> latch1.countDown());
        if (!latch1.await(1, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        assertEquals(Objects.requireNonNull(liveData1.getValue()).getAllRouteItems().size(), 0);
    }

    @Test
    public void deleteTempImages() throws InterruptedException {
        assertFalse(viewModel.isTempImagesDeleted);
        assert DataManager.getInstance() != null;
        viewModel.deleteTempImages(DataManager.getInstance().getAllRouteItems(), ApplicationProvider.getApplicationContext().getFilesDir());
        Thread.sleep(1000);
        assertTrue(viewModel.isTempImagesDeleted);
    }
}