package ru.mobnius.cic.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.LoadResultsTask;
import ru.mobnius.cic.ui.model.ResultItem;

public class PointInfoViewModel extends ViewModel {
    public int layoutPosition;
    @Nullable
    private MutableLiveData<List<ResultItem>> liveData;
    @Nullable
    private MainTaskExecutor executor;

    @NonNull
    public MutableLiveData<List<ResultItem>> getResults(final @NonNull String pointId) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        loadResults(pointId);
        return liveData;
    }

    private void loadResults(final @NonNull String pointId) {
        if (executor == null) {
            executor = new MainTaskExecutor();
        } else {
            executor.isRunning.set(false);
        }
        final LoadResultsTask loadResultsTask = new LoadResultsTask(pointId);
        executor.executeAsync(loadResultsTask, new MainTaskExecutorCallback<List<ResultItem>>() {
            @Override
            public void onCallableComplete(@NonNull List<ResultItem> results) {
                if (liveData == null) {
                    return;
                }
                liveData.postValue(results);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                if (liveData == null) {
                    return;
                }
                liveData.postValue(new ArrayList<>());
            }
        });
    }
}
