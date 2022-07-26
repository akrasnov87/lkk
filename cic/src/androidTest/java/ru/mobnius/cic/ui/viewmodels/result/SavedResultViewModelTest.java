package ru.mobnius.cic.ui.viewmodels.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.CONSTS;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.ResultItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.cic.ui.viewmodels.BaseModelTest;

public class SavedResultViewModelTest extends BaseModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private final SavedResultViewModel viewModel = new SavedResultViewModel();

    @Before
    public void before() {
        viewModel.pointItem = PointItem.getTestInstance();
        viewModel.pointItem.resultId = CONSTS.RESULT_ID;
        viewModel.initNewModel();

    }

    @Test
    public void getImageItems() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        final MutableLiveData<List<ImageItem>> liveData = viewModel.getImageItems(ApplicationProvider.getApplicationContext());
        liveData.observeForever(userProfile -> latch.countDown());
        if (!latch.await(1, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        assert viewModel.imageItemsData != null;
        int size = Objects.requireNonNull(viewModel.imageItemsData.getValue()).size();
        assertEquals(size, 4);
        viewModel.imagesShowing.clear();
    }

    @Test
    public void enableSaveButton() {
        assertFalse(viewModel.enableSaveButton());
        final ImageItem meter = ImageItem.createTestItem(3L);
        final ImageItem seal = ImageItem.createTestItem(2L);
        final ImageItem device = ImageItem.createTestItem(1L);
        viewModel.imagesShowing.add(meter);
        viewModel.imagesShowing.add(seal);
        viewModel.imagesShowing.add(device);
        viewModel.metersShowing.get(0).setCurrentValue(40000.0);
        assertTrue(viewModel.enableSaveButton());
        viewModel.imagesShowing.clear();
        viewModel.metersShowing.get(0).setCurrentValue(30000.0);
    }

    @Test
    public void save() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<SavedResult> liveData = viewModel.save(new Location("GPS"));
        liveData.observeForever(userProfile -> latch.countDown());
        if (!latch.await(1, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        assert DataManager.getInstance() != null;
        assert viewModel.pointItem != null;
        List<ResultItem> list = DataManager.getInstance().getResultItems(viewModel.pointItem.id);
        assertEquals(list.size(), 1);
    }
}