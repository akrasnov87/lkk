package ru.mobnius.cic.ui.viewmodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.CONSTS;
import ru.mobnius.cic.Names;
import ru.mobnius.cic.data.search.PointFilter;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;

public class PointViewModelTest extends BaseModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private final PointViewModel viewModel = new PointViewModel();

    @Before
    public void before() {
        viewModel.routeId = CONSTS.ROUTE_ID;
    }


    @Test
    public void setStatusFilter() {
        assertFalse(viewModel.doneFilter.isAdded());
        assertFalse(viewModel.undoneFilter.isAdded());
        viewModel.setStatusFilter(PointFilter.STATUS_DONE);
        assertTrue(viewModel.doneFilter.isAdded());
        viewModel.setStatusFilter(PointFilter.STATUS_ALL);
    }

    @Test
    public void setTypeFilter() {
        assertFalse(viewModel.companyTypeFilter.isAdded());
        assertFalse(viewModel.personTypeFilter.isAdded());
        viewModel.setTypeFilter(PointFilter.TYPE_PERSON);
        assertTrue(viewModel.personTypeFilter.isAdded());
        viewModel.setTypeFilter(PointFilter.TYPE_ALL);
    }

    @Test
    public void setAreaFilter() {
        assertTrue(viewModel.addressFilter.isAdded());
        assertTrue(viewModel.deviceFilter.isAdded());
        assertTrue(viewModel.subscrFilter.isAdded());
        viewModel.setAreaFilter(PointFilter.AREA_DEVICE_NUMBER);
        assertFalse(viewModel.subscrFilter.isAdded());
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }

    @Test
    public void countFilter() {
        assertEquals(viewModel.countFilter(), 0);
        viewModel.setAreaFilter(PointFilter.AREA_DEVICE_NUMBER);
        viewModel.setTypeFilter(PointFilter.TYPE_PERSON);
        assertEquals(viewModel.countFilter(), 2);
        viewModel.setTypeFilter(PointFilter.TYPE_ALL);
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void setNewState() throws InterruptedException {
        viewModel.loadedItems.add(PointItem.getTestInstance());
        assertEquals(viewModel.loadedItems.size(), 1);
        viewModel.setStatusFilter(PointFilter.STATUS_DONE);
        viewModel.setNewState();
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<List<PointItem>> liveData = viewModel.refreshPointItems();
        liveData.observeForever(userProfile -> latch.countDown());
        latch.await(3, TimeUnit.SECONDS);
        assert viewModel.pointItemsLiveData != null;
        assertEquals(Objects.requireNonNull(viewModel.pointItemsLiveData.getValue()).size(), 0);
        viewModel.setStatusFilter(PointFilter.STATUS_ALL);
        viewModel.setNewState();
        final CountDownLatch latch2 = new CountDownLatch(1);
        final MutableLiveData<List<PointItem>> liveData2 = viewModel.refreshPointItems();
        liveData2.observeForever(userProfile -> latch2.countDown());
        latch2.await(3, TimeUnit.SECONDS);
        Thread.sleep(1000);
        assertEquals(viewModel.pointItemsLiveData.getValue().size(), 1);
        viewModel.loadedItems.clear();
        viewModel.setStatusFilter(PointFilter.STATUS_ALL);
        viewModel.setNewState();
        viewModel.pointItemsLiveData = null;
    }

    @Test
    public void returnToState() {
        viewModel.setStatusFilter(PointFilter.STATUS_DONE);
        assertTrue(viewModel.doneFilter.isAdded());
        viewModel.returnToState();
        assertFalse(viewModel.doneFilter.isAdded());
    }

    @Test
    public void isDifferent() {
        assertFalse(viewModel.isDifferent());
        viewModel.setStatusFilter(PointFilter.STATUS_DONE);
        assertTrue(viewModel.isDifferent());
        viewModel.returnToState();
    }

    @Test
    public void isAllFields() {
        assertTrue(viewModel.isAllFields());
        viewModel.setAreaFilter(PointFilter.AREA_DEVICE_NUMBER);
        assertFalse(viewModel.isAllFields());
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }

    @Test
    public void isSubscrNumberOnly() {
        assertFalse(viewModel.isSubscrNumberOnly());
        viewModel.setAreaFilter(PointFilter.AREA_SUBSCR_NUMBER);
        assertTrue(viewModel.isSubscrNumberOnly());
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }

    @Test
    public void isDeviceNumberOnly() {
        assertFalse(viewModel.isDeviceNumberOnly());
        viewModel.setAreaFilter(PointFilter.AREA_DEVICE_NUMBER);
        assertTrue(viewModel.isDeviceNumberOnly());
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }

    @Test
    public void isAddressOnly() {
        assertFalse(viewModel.isAddressOnly());
        viewModel.setAreaFilter(PointFilter.AREA_ADDRESS);
        assertTrue(viewModel.isAddressOnly());
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }

    @Test
    public void isOwnerOnly() {
        assertFalse(viewModel.isOwnerOnly());
        viewModel.setAreaFilter(PointFilter.AREA_OWNER_NAME);
        assertTrue(viewModel.isOwnerOnly());
        viewModel.setAreaFilter(PointFilter.AREA_ALL);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void getPointItems() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<List<PointItem>> liveData = viewModel.getPointItems();
        liveData.observeForever(userProfile -> latch.countDown());
        latch.await(3, TimeUnit.SECONDS);
        assert viewModel.pointItemsLiveData != null;
        assertEquals(Objects.requireNonNull(viewModel.pointItemsLiveData.getValue()).size(), 1);
    }

    @Test
    public void getSortTypesData() {
        final ArrayList<Map<String, Object>> list = viewModel.getSortTypesData(ApplicationProvider.getApplicationContext());
        assertEquals(list.size(), 5);
        assertEquals(list.get(1).get(Names.ID), 1);
    }

    @Test
    public void setPointDone() {
        final SavedResult savedResult = new SavedResult(CONSTS.POINT_ID, CONSTS.RESULT_ID, true, "");
        viewModel.loadedItems.add(PointItem.getTestInstance());
        viewModel.setPointDone(savedResult);
        assertEquals(viewModel.loadedItems.get(0).resultId, CONSTS.RESULT_ID);
        assertTrue(viewModel.loadedItems.get(0).done);
        viewModel.loadedItems.clear();
    }

    @Test
    public void setPointUnDone() {
        final SavedResult savedResult = new SavedResult(CONSTS.POINT_ID, CONSTS.RESULT_ID, true, "");
        viewModel.loadedItems.add(PointItem.getTestInstance());
        viewModel.setPointUnDone(savedResult);
        assertNull(viewModel.loadedItems.get(0).resultId);
        assertFalse(viewModel.loadedItems.get(0).done);
        viewModel.loadedItems.clear();

    }
}