package ru.mobnius.cic.ui.viewmodels.result;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.concurent.LoadSavedImagesTask;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.UpdateResultTask;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.simple_core.utils.StringUtil;

public class SavedResultViewModel extends BaseResultViewModel {

    @NonNull
    @Override
    public MutableLiveData<List<ImageItem>> getImageItems(final @NonNull Context context) {
        if (imageItemsData == null) {
            imageItemsData = new MutableLiveData<>();
            loadingImageItemsData.setValue(true);
            loadImages(context);
        }
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
        return areImagesNotSame() || areMetersNotSame() || isMobileCauseNotSame() || isNoticeNotSame() || areHandledDocumentsNotSame();
    }

    @NonNull
    @Override
    public MutableLiveData<SavedResult> save(@NonNull Location location) {
        MutableLiveData<SavedResult> saveResultLiveData = new MutableLiveData<>();
        if (pointItem == null || StringUtil.isEmpty(pointItem.routeId)
                || StringUtil.isEmpty(pointItem.id) || StringUtil.isEmpty(pointItem.resultId)
                || StringUtil.isEmpty(getJbData(metersShowing, pointItem.version)) || canNotUpdateResult()) {
            return saveResultLiveData;
        }
        final Location pointLocation = new Location(MobniusApplication.APP_NAME);
        pointLocation.setLatitude(pointItem.latitude);
        pointLocation.setLongitude(pointItem.longitude);
        MainTaskExecutor executor = new MainTaskExecutor();
        UpdateResultTask updateResultTask = new UpdateResultTask(pointItem.routeId,
                pointItem.id,
                pointItem.resultId,
                location,
                pointLocation,
                getJbData(metersShowing, pointItem.version),
                imagesShowing,
                getStartActPicture().savedImages,
                StringUtil.defaultEmptyString(notice),
                getMobileCauseItem().mobileCauseId);
        executor.executeAsync(updateResultTask, new MainTaskExecutorCallback<SavedResult>() {
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

    /**
     * Асинхронная загрузка сохраненных ранее фотографий
     *
     * @return массив объектов ImageItem
     */
    private void loadImages(final @NonNull Context context) {
        if (imageItemsData == null || pointItem == null || StringUtil.isEmpty(pointItem.resultId)) {
            return;
        }
        final MainTaskExecutor executor = new MainTaskExecutor();
        final LoadSavedImagesTask loadSavedImagesTask = new LoadSavedImagesTask(context, pointItem.resultId);
        executor.executeAsync(loadSavedImagesTask, new MainTaskExecutorCallback<List<ImageItem>>() {
            @Override
            public void onCallableComplete(@NonNull List<ImageItem> result) {
                if (imageItemsData == null) {
                    return;
                }
                getStartActPicture().setSavedImages(new ArrayList<>(result));
                if (imagesShowing.size() == 0) {
                    imagesShowing.addAll(result);
                    imagesShowing.add(ImageItem.createPlaceholderItem());
                }
                loadingImageItemsData.postValue(false);
                imageItemsData.postValue(imagesShowing);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                if (imageItemsData == null) {
                    return;
                }
                loadingImageItemsData.postValue(false);
                imageItemsData.postValue(imagesShowing);
            }
        });
        imageItemsData.setValue(imagesShowing);
    }

    /**
     * Проверка на совпадение видимых фотографий с сохраненными в БД,
     * проверяется на соответсвие элементов в массиве, совпадение
     * идентификаторов типов фотографий и комментария к фото
     *
     * @return true если фото отличаются
     */
    private boolean areImagesNotSame() {
        if (getStartActPicture().savedImages.size() != getImagesShowingSize()) {
            //длина массивов разная значит не равны
            return true;
        }
        return getStartActPicture().compareImagesNotSame(imagesShowing);

    }

    /**
     * Возвращает количество видимых фотографий.
     * Вычитается единица, потому что в списке есть Placeholder.
     *
     * @return количество фото
     */
    private int getImagesShowingSize() {
        return imagesShowing.size() - 1;
    }
}
