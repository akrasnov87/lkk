package ru.mobnius.cic.concurent;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.MeterItem;

/**
 * Класс загрузки входящих показаний
 * в дополнительном потоке
 */
public class LoadMeterItemsTask implements Callable<List<MeterItem>> {
    @NonNull
    private final String pointId;

    public LoadMeterItemsTask(@NonNull String pointId) {
        this.pointId = pointId;
    }

    @NonNull
    @Override
    public List<MeterItem> call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new ArrayList<>();
        }
        return DataManager.getInstance().getInputMeters(pointId);
    }
}
