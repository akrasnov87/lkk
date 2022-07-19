package ru.mobnius.cic.ui.viewmodels.result;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.Names;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.ProcessNewImageTask;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.models.Causes;
import ru.mobnius.cic.data.storage.models.Results;
import ru.mobnius.cic.ui.model.FailureImageObject;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.cic.ui.model.MeterItem;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.cic.ui.model.StartActPicture;
import ru.mobnius.cic.ui.model.concurent.MobileCauseItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.cic.ui.verification.VerificationManager;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.DoubleUtil;
import ru.mobnius.simple_core.utils.JsonUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public abstract class BaseResultViewModel extends ViewModel {
    @Nullable
    public StartActPicture startActPicture;
    @Nullable
    public ImageType selectedImageType;
    @Nullable
    public String absFilePath;
    @Nullable
    public String fileName;
    @Nullable
    public PointItem pointItem;
    @Nullable
    public ImageItem imageViewShowingItem;
    @Nullable
    private MutableLiveData<List<MeterItem>> meterItemsData;
    @Nullable
    protected MutableLiveData<List<ImageItem>> imageItemsData;
    @Nullable
    public MobileCauseItem mobileCauseItem;
    @Nullable
    public String notice;
    @NonNull
    public final MutableLiveData<Boolean> loadingImageItemsData = new MutableLiveData<>();
    @NonNull
    public final List<ImageItem> imagesShowing = new ArrayList<>();
    @NonNull
    public List<MeterItem> metersShowing = new ArrayList<>();
    @NonNull
    public List<FailureImageObject> failureImageDictionary = new ArrayList<>();
    @NonNull
    public VerificationManager verificationManager = new VerificationManager();
    public boolean isReceipt;
    public boolean isNotificationOrp;
    private boolean isInitialized = false;

    public void initNewModel() {
        if (isInitialized) {
            return;
        }
        this.startActPicture = new StartActPicture();
        this.notice = StringUtil.EMPTY;
        this.mobileCauseItem = new MobileCauseItem();
        this.metersShowing = new ArrayList<>();
        initNotice();
        initMobileCauseItem();
        initDocumentsHanded();
        initFailureImageDictionary();
        initMeterItems();
        isInitialized = true;
    }

    @SuppressWarnings("StringOperationCanBeSimplified")
    private void initNotice() {
        if (DataManager.getInstance() == null || pointItem == null || StringUtil.isEmpty(pointItem.resultId)) {
            getStartActPicture().savedNotice = StringUtil.EMPTY;
            notice = StringUtil.EMPTY;
            return;
        }
        final Results result = DataManager.getInstance().daoSession.getResultsDao().load(pointItem.resultId);
        if (result == null || StringUtil.isEmpty(result.c_notice)) {
            getStartActPicture().savedNotice = StringUtil.EMPTY;
            notice = StringUtil.EMPTY;
            return;
        }
        getStartActPicture().savedNotice = new String(result.c_notice);
        notice = new String(result.c_notice);
    }

    private void initMobileCauseItem() {
        if (pointItem == null
                || StringUtil.isEmpty(pointItem.resultId)
                || DataManager.getInstance() == null) {
            return;
        }
        final Results result = DataManager.getInstance().daoSession.getResultsDao().load(pointItem.resultId);
        if (result == null) {
            return;
        }
        final Causes cause = DataManager.getInstance().getMobileFailureCause(pointItem.resultId);
        if (cause == null) {
            return;
        }

        if (mobileCauseItem == null) {
            mobileCauseItem = new MobileCauseItem();
        }
        mobileCauseItem.initCauseId(cause);
        getStartActPicture().mobileCauseId = mobileCauseItem.mobileCauseId;
    }

    private void initDocumentsHanded() {
        if (pointItem == null || StringUtil.isEmpty(pointItem.resultId) || DataManager.getInstance() == null) {
            return;
        }
        isReceipt = DataManager.getInstance().getIsReceipt(pointItem.resultId);
        getStartActPicture().isReceipt = isReceipt;
        isNotificationOrp = DataManager.getInstance().getIsNotificationOpr(pointItem.resultId);
        getStartActPicture().isNotificationOrp = isNotificationOrp;
    }

    private void initFailureImageDictionary() {
        if (DataManager.getInstance() == null || pointItem == null) {
            return;
        }
        failureImageDictionary = DataManager.getInstance().getFailureImageDictionary(pointItem.id);
    }

    private void initMeterItems() {
        if (pointItem == null || DataManager.getInstance() == null) {
            metersShowing = new ArrayList<>();
            return;
        }
        metersShowing = DataManager.getInstance().getInputMeters(pointItem.id);
        getStartActPicture().metersShowing = metersShowing;
    }

    @NonNull
    public MutableLiveData<ImageItem> processNewImage(final @NonNull Location location) {
        final MutableLiveData<ImageItem> itemMutableLiveData = new MutableLiveData<>();
        if (StringUtil.isEmpty(fileName) || StringUtil.isEmpty(absFilePath) || selectedImageType == null || canNotUpdateResult()) {
            return itemMutableLiveData;
        }
        final MainTaskExecutor executor = new MainTaskExecutor();
        final ProcessNewImageTask task = new ProcessNewImageTask(fileName, absFilePath, selectedImageType, location);
        executor.executeAsync(task, new MainTaskExecutorCallback<ImageItem>() {
            @Override
            public void onCallableComplete(@Nullable ImageItem result) {
                if (result == null) {
                    return;
                }
                imagesShowing.add(result);
                itemMutableLiveData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {

            }
        });
        return itemMutableLiveData;
    }


    @NonNull
    public MutableLiveData<List<MeterItem>> getMeterItems() {
        if (meterItemsData == null) {
            meterItemsData = new MutableLiveData<>();
            meterItemsData.setValue(metersShowing);
        }
        return meterItemsData;

    }

    public void updateMeter(final @Nullable String value, final @NonNull String meterId) {
        if (canNotUpdateResult()) {
            return;
        }
        for (final MeterItem meterItem : metersShowing) {
            if (StringUtil.equals(meterItem.id, meterId)) {
                meterItem.setCurrentValue(DoubleUtil.getDoubleOrNull(value));
                meterItem.currentValueText = StringUtil.defaultEmptyString(value);
            }
        }
    }

    protected boolean notMadeNessesaryImages() {
        final List<FailureImageObject> failureImageObjects = getFailureImageObjectsByFailureId();
        if (failureImageObjects.size() == 0) {
            //что то явно пошло не так
            return true;
        }
        for (final FailureImageObject failureImageObject : failureImageObjects) {
            int currentCount = 0;
            for (final ImageItem item : imagesShowing) {
                if (failureImageObject.imageTypeId == item.imageType.typeId) {
                    currentCount++;
                }
            }
            if (currentCount < failureImageObject.count) {
                return true;
            }
        }
        return false;
    }

    public boolean canNotUpdateResult() {
        if (pointItem == null) {
            return false;
        }
        return pointItem.sync && !pointItem.reject;
    }

    public void updateImage(final @NonNull ImageType result) {
        if (canNotUpdateResult()) {
            return;
        }
        for (final ImageItem imageItem : imagesShowing) {
            if (StringUtil.equals(imageItem.id, result.imageId)) {
                imageItem.imageType = result;
            }
        }
    }

    public void getImageItem(final @NonNull String imageId, final @NonNull Bitmap defaultErrorIcon) {
        for (final ImageItem item : imagesShowing) {
            if (StringUtil.equals(item.id, imageId)) {
                imageViewShowingItem = item;
                return;
            }
        }
        imageViewShowingItem = ImageItem.createErrorItem(defaultErrorIcon);
    }

    public void disableImage(final @NonNull String imageId) {
        if (canNotUpdateResult()) {
            return;
        }
        final Iterator<ImageItem> iterator = imagesShowing.iterator();
        while (iterator.hasNext()) {
            if (StringUtil.equals(iterator.next().id, imageId)) {
                iterator.remove();
                break;
            }
        }

    }

    @NonNull
    protected String getJbData(final @NonNull List<MeterItem> meterItems, final int version) {
        if (meterItems.size() == 0) {
            return StringUtil.EMPTY;
        }
        final JSONArray array = new JSONArray();
        for (final MeterItem meter : meterItems) {
            final JSONObject meterJson = meter.getNewMeterJson();
            array.put(meterJson);
        }
        try {
            final JSONObject object = new JSONObject();

            final JSONObject data = new JSONObject();
            data.put(JsonUtil.METER_JSON_KEY, array);
            data.put(JsonUtil.RECEIPT_JSON_KEY, isReceipt);
            data.put(JsonUtil.NOTIFICATION_ORP_JSON_KEY, isNotificationOrp);

            object.put(JsonUtil.DATA_JSON_KEY, data);

            final JSONObject versionJson = new JSONObject();
            versionJson.put(JsonUtil.VERSION_JSON_KEY, String.valueOf((double) version));

            object.put(JsonUtil.META_JSON_KEY, versionJson);
            return object.toString();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return StringUtil.EMPTY;
        }
    }

    @NotNull
    public ArrayList<Map<String, Object>> getMobileCausesList() {
        if (DataManager.getInstance() == null) {
            return new ArrayList<>();
        }
        final ArrayList<Map<String, Object>> failureReasons = new ArrayList<>();
        final List<Causes> causes = DataManager.getInstance().getMobileCauses();
        for (final Causes cause : causes) {
            final Map<String, Object> causeMap = new HashMap<>();
            causeMap.put(Names.ID, cause.id);
            causeMap.put(Names.NAME, cause.c_name);
            failureReasons.add(causeMap);
        }
        return failureReasons;
    }

    protected boolean areMetersNotSame() {
        if (getMobileCauseItem().mobileCauseId != LongUtil.MINUS) {
            return false;
        }
        for (final MeterItem editedItem : metersShowing) {
            if (editedItem.getCurrentValue() != null && editedItem.getCurrentValue() != editedItem.startValue) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMobileCauseNotSame() {
        return getMobileCauseItem().mobileCauseId != getStartActPicture().mobileCauseId;
    }

    protected boolean isNoticeNotSame() {
        return StringUtil.notEquals(getStartActPicture().savedNotice, notice);
    }

    protected boolean areHandledDocumentsNotSame() {
        return getStartActPicture().isReceipt != isReceipt || getStartActPicture().isNotificationOrp != isNotificationOrp;
    }

    public boolean isMobileCauseSelected() {
        return getMobileCauseItem().mobileCauseId != LongUtil.MINUS;
    }

    @NonNull
    public String getRejectMessage() {
        if (DataManager.getInstance() == null || pointItem == null || StringUtil.isEmpty(pointItem.resultId)) {
            return StringUtil.EMPTY;
        }
        return DataManager.getInstance().getServerFailureCause(pointItem.resultId);
    }

    @NonNull
    public String getPhotoValidationMessage() {
        if (canNotUpdateResult()) {
            return MobniusApplication.CAN_NOT_EDIT_SYNCED_RESULT;
        }
        boolean allPhotosMade = true;
        final StringBuilder notAllPhotoMessage = new StringBuilder(MobniusApplication.MUST_MAKE_PHOTO_OF);
        for (final FailureImageObject failureImageObject : getFailureImageObjectsByFailureId()) {
            int realCount = 0;
            for (final ImageItem imageItem : imagesShowing) {
                if (failureImageObject.imageTypeId == imageItem.imageType.typeId) {
                    realCount++;
                }
            }
            if (realCount < failureImageObject.count) {
                allPhotosMade = false;
                notAllPhotoMessage.append(failureImageObject.count).append(StringUtil.SPACE).append(failureImageObject.name).append("\n");
            }
        }

        if (allPhotosMade) {
            return MobniusApplication.ALL_NECESSARY_PHOTOS_ARE_MADE;
        }
        notAllPhotoMessage.deleteCharAt(notAllPhotoMessage.length() - 1);
        return notAllPhotoMessage.toString();
    }

    @NonNull
    public String getCurrentDate() {
        if (metersShowing.size() > 0) {
            final MeterItem meterItem = metersShowing.get(0);
            if (meterItem.currentDate != null) {
                return DateUtil.getNonNullDateTextShort(meterItem.currentDate);
            }
        }
        return DateUtil.getNonNullDateTextShort(new Date());
    }

    public List<FailureImageObject> getFailureImageObjectsByFailureId() {
        final List<FailureImageObject> filteredList = new ArrayList<>();
        for (final FailureImageObject failureImageObject : failureImageDictionary) {
            if (failureImageObject.failureId == getMobileCauseItem().mobileCauseId) {
                filteredList.add(failureImageObject);
            }
        }
        return filteredList;
    }

    @NonNull
    public StartActPicture getStartActPicture() {
        if (startActPicture == null) {
            startActPicture = new StartActPicture();
        }
        return startActPicture;
    }

    @NonNull
    public MobileCauseItem getMobileCauseItem() {
        if (mobileCauseItem == null) {
            mobileCauseItem = new MobileCauseItem();
            initMobileCauseItem();
        }
        return mobileCauseItem;
    }

    @NonNull
    public abstract MutableLiveData<SavedResult> save(final @NonNull Location location);

    @NonNull
    public abstract MutableLiveData<List<ImageItem>> getImageItems(final @NonNull Context context);

    public abstract boolean enableSaveButton();


    public void destroy() {
        imagesShowing.clear();
        metersShowing.clear();
        startActPicture = null;
        selectedImageType = null;
        absFilePath = null;
        fileName = null;
        pointItem = null;
        notice = null;
        imageViewShowingItem = null;
        meterItemsData = null;
        imageItemsData = null;
        mobileCauseItem = null;
        isInitialized = false;
        isReceipt = false;
        isNotificationOrp = false;
    }

}