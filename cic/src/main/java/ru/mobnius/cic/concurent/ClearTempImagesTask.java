package ru.mobnius.cic.concurent;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.simple_core.utils.FileUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс для очистки файловой системы от ненужных фотографий
 */
public class ClearTempImagesTask implements Callable<Void> {
    @NonNull
    private final List<RouteItem> routeItems;
    @NonNull
    private final File appDir;

    public ClearTempImagesTask(final @NonNull List<RouteItem> routeItems, final @NonNull File appDir) {
        this.routeItems = routeItems;
        this.appDir = appDir;
    }

    @Override
    public Void call() throws Exception {
        if (DataManager.getInstance() == null) {
            return null;
        }
        final File picturesSubfolder = FileUtil.getAppSubfolder(appDir, FileUtil.PICTURES_SUBFOLDER);
        final File[] files = picturesSubfolder.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        final List<File> temp = new ArrayList<>();
        final List<String> savedImagesPaths = new ArrayList<>();
        for (final RouteItem routeItem : routeItems) {
            for (final PointItem pointItem : DataManager.getInstance().getRoutePointItems(routeItem.id)) {
                if (StringUtil.isEmpty(pointItem.resultId)) {
                    continue;
                }
                savedImagesPaths.addAll(DataManager.getInstance().loadSavedImagesPaths(pointItem.resultId));
            }
        }
        for (final File file : files) {
            boolean notContains = true;
            for (final String filePath : savedImagesPaths) {
                if (StringUtil.equalsIgnoreCase(file.getAbsolutePath(), filePath)) {
                    notContains = false;
                }
            }
            if (notContains) {
                temp.add(file);
            }
        }
        for (final File fileToDelete : temp) {
            FileUtil.deleteQuietly(fileToDelete);
        }
        return null;
    }
}
