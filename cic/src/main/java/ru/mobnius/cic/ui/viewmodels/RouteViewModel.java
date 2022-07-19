package ru.mobnius.cic.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import ru.mobnius.cic.concurent.ClearTempImagesTask;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.ui.model.concurent.LoadRoutesResult;
import ru.mobnius.cic.concurent.LoadRoutesTask;
import ru.mobnius.cic.concurent.LoadWithSearchTask;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.simple_core.utils.StringUtil;

public class RouteViewModel extends ViewModel {
    public boolean isUnsyncMessageShown = false;
    public int unsyncCount = 0;
    @Nullable
    private MutableLiveData<LoadRoutesResult> routeItemsData;
    @NonNull
    public final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    @Nullable
    private MainTaskExecutor executor;
    @NonNull
    public String query = StringUtil.EMPTY;
    @NonNull
    public LoadRoutesResult currentResult = new LoadRoutesResult();

    public boolean isTempImagesDeleted = false;


    public @NonNull
    MutableLiveData<LoadRoutesResult> refreshRoutes() {
        if (routeItemsData == null) {
            routeItemsData = new MutableLiveData<>();
        }
        loadRoutes();
        return routeItemsData;

    }


    private void loadRoutes() {
        if (executor != null) {
            executor = null;
        }
        loadingLiveData.setValue(true);
        executor = new MainTaskExecutor();
        final LoadRoutesTask loadRoutesTask = new LoadRoutesTask();
        executor.executeAsync(loadRoutesTask, new MainTaskExecutorCallback<LoadRoutesResult>() {
            @Override
            public void onCallableComplete(@NonNull LoadRoutesResult result) {
                if (routeItemsData == null) {
                    return;
                }
                currentResult = result;
                loadingLiveData.postValue(false);
                routeItemsData.postValue(result);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                if (routeItemsData == null) {
                    return;
                }
                loadingLiveData.postValue(false);
                routeItemsData.postValue(currentResult);
            }
        });
    }

    @NonNull
    public MutableLiveData<LoadRoutesResult> searchAsync() {
        if (executor != null) {
            executor = null;
        }
        executor = new MainTaskExecutor();
        if (routeItemsData == null) {
            routeItemsData = new MutableLiveData<>();
        }
        loadingLiveData.setValue(true);
        final LoadWithSearchTask loadRoutesTask = new LoadWithSearchTask(query);
        executor.executeAsync(loadRoutesTask, new MainTaskExecutorCallback<LoadRoutesResult>() {
            @Override
            public void onCallableComplete(@NonNull LoadRoutesResult result) {
                if (routeItemsData == null) {
                    return;
                }
                routeItemsData.postValue(result);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                if (routeItemsData == null) {
                    return;
                }
                routeItemsData.postValue(currentResult);
                loadingLiveData.setValue(false);
            }
        });
        return routeItemsData;
    }

    /**
     * Метод для удаления сохраненных файлов фотографий,
     * которые не были сохранены в актах. Решил вызывать этот метод здесь,
     * так как во фрагменте карточки нет гарантий что onDestroy будет вызван,
     * соответственно со временем память телефона может начать
     * засоряться ненужными фотографиями
     *
     * @param routeItems список актуальных точек маршрута
     * @param appDir     приватная дирректория приложения для сохранения файлов
     */
    public void deleteTempImages(final @NonNull List<RouteItem> routeItems, final @Nullable File appDir) {
        if (appDir == null || isTempImagesDeleted) {
            return;
        }
        final MainTaskExecutor executor = new MainTaskExecutor();
        final ClearTempImagesTask clearTempImagesTask = new ClearTempImagesTask(routeItems, appDir);
        executor.executeAsync(clearTempImagesTask, new MainTaskExecutorCallback<Void>() {
            @Override
            public void onCallableComplete(@NonNull Void result) {
                isTempImagesDeleted = true;
            }

            @Override
            public void onCallableError(@NonNull String error) {

            }
        });
    }

}
