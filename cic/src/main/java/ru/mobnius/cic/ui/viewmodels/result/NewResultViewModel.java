package ru.mobnius.cic.ui.viewmodels.result;


import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.SaveNewResultTask;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class NewResultViewModel extends BaseResultViewModel {


    @NonNull
    @Override
    public MutableLiveData<List<ImageItem>> getImageItems(final @NonNull Context context) {
        if (imageItemsData == null) {
            imageItemsData = new MutableLiveData<>();
            imagesShowing.add(ImageItem.createPlaceholderItem());
        }
        loadImages();
        return imageItemsData;
    }


    @Override
    public boolean enableSaveButton() {
        if (canNotUpdateResult()) {
            return false;
        }
        if (!verificationManager.verifyInvisible(getMobileCauseItem().isFailureNotSelected())) {
            return false;
        }
        if (notMadeNessesaryImages()) {
            return false;
        }
        return areMetersNotSame() || isMobileCauseNotSame() || isNoticeNotSame() || areHandledDocumentsNotSame();
    }

    @NonNull
    @Override
    public MutableLiveData<SavedResult> save(final @NonNull Location location) {
        final MutableLiveData<SavedResult> saveResultLiveData = new MutableLiveData<>();
        if (pointItem == null || StringUtil.isEmpty(pointItem.routeId) || StringUtil.isEmpty(pointItem.id) || canNotUpdateResult()
                || StringUtil.isEmpty(getJbData(metersShowing, pointItem.version))) {
            return saveResultLiveData;
        }
        final Location pointLocation = new Location(MobniusApplication.APP_NAME);
        pointLocation.setLatitude(pointItem.latitude);
        pointLocation.setLongitude(pointItem.longitude);
        final MainTaskExecutor executor = new MainTaskExecutor();
        final SaveNewResultTask saveNewResultTask = new SaveNewResultTask(pointItem.id,
                pointItem.routeId,
                location,
                pointLocation,
                getJbData(metersShowing, pointItem.version),
                imagesShowing,
                StringUtil.defaultEmptyString(notice),
                getMobileCauseItem().mobileCauseId);
        executor.executeAsync(saveNewResultTask, new MainTaskExecutorCallback<SavedResult>() {
            @Override
            public void onCallableComplete(@NonNull SavedResult result) {
                saveResultLiveData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                saveResultLiveData.postValue(new SavedResult(StringUtil.EMPTY, StringUtil.EMPTY, false, error));
            }
        });
        return saveResultLiveData;
    }


    private void loadImages() {
        if (imageItemsData == null) {
            return;
        }
        getStartActPicture().setSavedImages(new ArrayList<>());
        imageItemsData.setValue(imagesShowing);
    }


}
