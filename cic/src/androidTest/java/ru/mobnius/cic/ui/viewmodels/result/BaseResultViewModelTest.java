package ru.mobnius.cic.ui.viewmodels.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.CONSTS;
import ru.mobnius.cic.Names;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.cic.ui.model.MeterItem;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.cic.ui.viewmodels.BaseModelTest;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class BaseResultViewModelTest extends BaseModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();


    private final BaseResultViewModel viewModel = new BaseResultViewModel() {
        @NonNull
        @Override
        public MutableLiveData<SavedResult> save(@NonNull Location location) {
            return mock(MutableLiveData.class);
        }

        @NonNull
        @Override
        public MutableLiveData<List<ImageItem>> getImageItems(@NonNull Context context) {
            return mock(MutableLiveData.class);
        }

        @Override
        public boolean enableSaveButton() {
            return false;
        }
    };

    @Before
    public void before() {
        viewModel.pointItem = PointItem.getTestInstance();
        viewModel.initNewModel();
    }


    @Test
    public void initNewModel() {
        assertEquals(viewModel.notice, CONSTS.C_NOTICE);
        assert viewModel.mobileCauseItem != null;
        assertEquals(viewModel.mobileCauseItem.mobileCauseId, LongUtil.MINUS);
        assertNotNull(viewModel.startActPicture);
        assertEquals(viewModel.metersShowing.size(), 1);
        assertEquals(viewModel.metersShowing.get(0).currentValueText, CONSTS.METER_TEXT_VALUE);

    }

    @Test
    public void processNewImage() throws FileNotFoundException, InterruptedException {
        final File dir = ApplicationProvider.getApplicationContext().getFilesDir();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new FileNotFoundException("Can not create dir");
            }
        }
        final String fileName = "photo.jpg";
        final File photo = new File(dir, fileName);
        final String absFilePath = photo.getAbsolutePath();
        final ImageType imageType = new ImageType(1L, "Прибор", "METER", StringUtil.EMPTY, false);
        final Location location = new Location("GPS");
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
        try (FileOutputStream out = new FileOutputStream(photo)) {
            emptyBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewModel.absFilePath = absFilePath;
        viewModel.fileName = fileName;
        viewModel.selectedImageType = imageType;
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableLiveData<ImageItem> liveData = viewModel.processNewImage(new Location("GPS"));
        liveData.observeForever(userProfile -> latch.countDown());
        if (!latch.await(1, TimeUnit.SECONDS)) {
            throw new InterruptedException("CountDownLatch did not await");
        }
        assertEquals(Objects.requireNonNull(liveData.getValue()).imageType, imageType);
        assertEquals(Objects.requireNonNull(liveData.getValue()).absFilePath, absFilePath);
        assertEquals(Objects.requireNonNull(liveData.getValue()).fileName, fileName);
        assertEquals(Objects.requireNonNull(liveData.getValue()).location.getProvider(), location.getProvider());
        if (!photo.delete()) {
            throw new FileNotFoundException("Can not delete file photo.jpg");
        }

    }

    @Test
    public void getMeterItems() {
        final List<MeterItem> meterItems = viewModel.metersShowing;
        assertEquals(viewModel.getMeterItems().getValue(), meterItems);
    }

    @Test
    public void updateMeter() {
        final String newValue = "40000";
        viewModel.updateMeter(newValue, CONSTS.METER_ID);
        assertEquals(viewModel.metersShowing.get(0).currentValueText, newValue);
        viewModel.updateMeter(CONSTS.METER_TEXT_VALUE, CONSTS.METER_ID);
        assertEquals(viewModel.metersShowing.get(0).currentValueText, CONSTS.METER_TEXT_VALUE);
    }

    @Test
    public void notMadeNessesaryImages() {
        assertTrue(viewModel.notMadeNessesaryImages());
        final ImageItem meter = ImageItem.createTestItem(3L);
        final ImageItem seal = ImageItem.createTestItem(2L);
        final ImageItem device = ImageItem.createTestItem(1L);
        viewModel.imagesShowing.add(meter);
        viewModel.imagesShowing.add(seal);
        viewModel.imagesShowing.add(device);
        assertFalse(viewModel.notMadeNessesaryImages());
        viewModel.imagesShowing.clear();
    }

    @Test
    public void canNotUpdateResult() {
        assertFalse(viewModel.canNotUpdateResult());
        assert viewModel.pointItem != null;
        viewModel.pointItem.sync = true;
        assertTrue(viewModel.canNotUpdateResult());
        viewModel.pointItem.reject = true;
        assertFalse(viewModel.canNotUpdateResult());
        viewModel.pointItem.sync = false;
        viewModel.pointItem.reject = false;
    }

    @Test
    public void updateImage() {
        final ImageItem device = ImageItem.createTestItem(1L);
        viewModel.imagesShowing.add(device);
        final ImageType imageType = new ImageType(2L, "Прибор", "DEVICE", StringUtil.EMPTY, false);
        imageType.imageId = device.id;
        viewModel.updateImage(imageType);
        assertEquals(viewModel.imagesShowing.get(0).imageType.typeId, 2L);
        viewModel.imagesShowing.clear();
    }

    @Test
    public void getImageItem() {
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
        final Bitmap errorBitmap = Bitmap.createBitmap(2, 2, config);
        final String imageId = UUID.randomUUID().toString();
        final ImageType imageType = new ImageType(2L, "Прибор", "DEVICE", StringUtil.EMPTY, false);
        final ImageItem imageItem = new ImageItem(imageId, StringUtil.EMPTY, StringUtil.EMPTY, new Date(), emptyBitmap, imageType, new Location("GPS"), false);
        viewModel.imagesShowing.add(imageItem);
        viewModel.getImageItem("", errorBitmap);
        assert viewModel.imageViewShowingItem != null;
        assertEquals(viewModel.imageViewShowingItem.id, ImageItem.ERROR_ID);
        assertEquals(viewModel.imageViewShowingItem.bitmap, errorBitmap);
        viewModel.getImageItem(imageId, errorBitmap);
        assertEquals(viewModel.imageViewShowingItem.id, imageId);
        assertEquals(viewModel.imageViewShowingItem.bitmap, emptyBitmap);
        viewModel.imagesShowing.clear();
        viewModel.imageViewShowingItem = null;

    }

    @Test
    public void disableImage() {
        final ImageItem device = ImageItem.createTestItem(1L);
        final String imageItemId = device.id;
        viewModel.imagesShowing.add(device);
        assertEquals(viewModel.imagesShowing.size(), 1);
        viewModel.disableImage(imageItemId);
        assertEquals(viewModel.imagesShowing.size(), 0);
    }

    @Test
    public void getJbData() {
        assertTrue(viewModel.getJbData(viewModel.metersShowing, 1).contains(CONSTS.RESULT_JB_DATA_CONTAINS_STRING));
    }

    @Test
    public void getMobileCausesList() {
        final ArrayList<Map<String, Object>> list = viewModel.getMobileCausesList();
        assertTrue(list.size() > 0);
        assertEquals(list.get(0).get(Names.ID), 3L);
    }

    @Test
    public void areMetersNotSame() {
        assertFalse(viewModel.areMetersNotSame());
        viewModel.metersShowing.get(0).setCurrentValue(40000.0);
        assertTrue(viewModel.areMetersNotSame());
        viewModel.metersShowing.get(0).setCurrentValue(30000.0);
        assertFalse(viewModel.areMetersNotSame());
    }

    @Test
    public void isMobileCauseNotSame() {
        assertFalse(viewModel.isMobileCauseNotSame());
        assert viewModel.mobileCauseItem != null;
        viewModel.mobileCauseItem.mobileCauseId = 4L;
        assertTrue(viewModel.isMobileCauseNotSame());
        viewModel.mobileCauseItem.mobileCauseId = -1L;
        assertFalse(viewModel.isMobileCauseNotSame());
    }

    @Test
    public void isNoticeNotSame() {
        assertFalse(viewModel.isNoticeNotSame());
        viewModel.notice = "test";
        assertTrue(viewModel.isNoticeNotSame());
        viewModel.notice = CONSTS.C_NOTICE;
        assertFalse(viewModel.isNoticeNotSame());
    }

    @Test
    public void areHandledDocumentsNotSame() {
        assertFalse(viewModel.areHandledDocumentsNotSame());
        viewModel.isReceipt = true;
        assertTrue(viewModel.areHandledDocumentsNotSame());
        viewModel.isReceipt = false;
        assertFalse(viewModel.areHandledDocumentsNotSame());
    }

    @Test
    public void isMobileCauseSelected() {
        assertFalse(viewModel.isMobileCauseSelected());
    }

    @Test
    public void getRejectMessage() {
        assertEquals(viewModel.getRejectMessage(), "");
    }

    @Test
    public void getPhotoValidationMessage() {
        assertTrue(viewModel.getPhotoValidationMessage().contains("Необходимо сделать следующие фото:"));
        final ImageItem meter = ImageItem.createTestItem(3L);
        final ImageItem seal = ImageItem.createTestItem(2L);
        final ImageItem device = ImageItem.createTestItem(1L);
        viewModel.imagesShowing.add(meter);
        viewModel.imagesShowing.add(seal);
        viewModel.imagesShowing.add(device);
        assertEquals(viewModel.getPhotoValidationMessage(), "Все необходимые фото сделаны");
        viewModel.imagesShowing.clear();
    }

    @Test
    public void getCurrentDate() {
        String testDateString = "";
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            testDateString =  dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(viewModel.getCurrentDate(), testDateString);
    }

    @Test
    public void getFailureImageObjectsByFailureId() {
        assertEquals(viewModel.getFailureImageObjectsByFailureId().size(), 3);
    }

    @Test
    public void getStartActPicture() {
        assertNotNull(viewModel.getStartActPicture());
    }

    @Test
    public void getMobileCauseItem() {
        assertNotNull(viewModel.getMobileCauseItem());
    }

}