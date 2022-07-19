package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.ResultItem;

/**
 * Класс загрузки сохраненных результатов
 * в дополнительном потоке
 */
public class LoadResultsTask implements Callable<List<ResultItem>> {
    @NonNull
    private final String pointId;

    public LoadResultsTask(final @NonNull String pointId) {
        this.pointId = pointId;
    }

    @NonNull
    @Override
    public List<ResultItem> call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new ArrayList<>();
        }
        return DataManager.getInstance().getResultItems(pointId);
    }
}