package ru.mobnius.cic.concurent;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.ImageItem;

/**
 * Класс загрузки фотографий из файловой системы или из сети
 * в дополнительном потоке
 */
public class LoadSavedImagesTask implements Callable<List<ImageItem>> {
    @NonNull
    private final String resultId;
    @NonNull
    private final Context context;

    public LoadSavedImagesTask(final @NonNull Context context,
                               final @NonNull String resultId) {
        this.resultId = resultId;
        this.context = context;
    }

    @NonNull
    @Override
    public List<ImageItem> call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new ArrayList<>();
        }
        return DataManager.getInstance().loadImageItems(context, resultId);
    }
}
