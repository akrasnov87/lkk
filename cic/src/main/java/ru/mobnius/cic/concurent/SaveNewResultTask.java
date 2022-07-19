package ru.mobnius.cic.concurent;

import android.location.Location;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Callable;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.AuditUtil;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс первичного сохранения выполненного задания
 * в дополнительном потоке
 */
public class SaveNewResultTask implements Callable<SavedResult> {
    @NonNull
    private final String pointId;
    @NonNull
    private final String routeId;
    @NonNull
    private final Location userLocation;
    @NonNull
    private final Location pointLocation;
    @NonNull
    private final String jbData;
    @NonNull
    private final String notice;
    @NonNull
    private final List<ImageItem> imageItems;
    private final long mobileCauseId;

    public SaveNewResultTask(final @NonNull String pointId,
                             final @NonNull String routeId,
                             final @NonNull Location userLocation,
                             final @NonNull Location pointLocation,
                             final @NonNull String jbData,
                             final @NonNull List<ImageItem> imageItems,
                             final @NonNull String notice,
                             final long mobileCauseId) {
        this.pointId = pointId;
        this.routeId = routeId;
        this.userLocation = userLocation;
        this.pointLocation = pointLocation;
        this.jbData = jbData;
        this.imageItems = imageItems;
        this.notice = notice;
        this.mobileCauseId = mobileCauseId;
    }


    @NonNull
    @Override
    public SavedResult call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new SavedResult(StringUtil.EMPTY, StringUtil.EMPTY, false, MobniusApplication.ERROR_NO_DB_CONNECTION);
        }
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return new SavedResult(StringUtil.EMPTY, StringUtil.EMPTY, false, MobniusApplication.ERROR_NO_AUTHORIZATION_DATA);
        }
        final long userId = Authorization.getInstance().user.getUserId();

        final String resultId = DataManager.getInstance().createResult(pointId,
                routeId,
                userId,
                jbData,
                userLocation,
                pointLocation,
                notice,
                mobileCauseId);
        AuditUtil.writeAudit(AuditUtil.CREATE_RESULT, resultId, StringUtil.EMPTY);

        for (final ImageItem item : imageItems) {
            DataManager.getInstance().saveImage(item, resultId, userLocation, pointId, routeId, userId);
        }
        AuditUtil.writeAudit(AuditUtil.FIRST_SAVE_PHOTOS, BaseApp.SAVED + imageItems.size(), StringUtil.EMPTY);
        return new SavedResult(pointId, resultId, true, MobniusApplication.TASK_SAVED_SUCCESSFULLY);
    }
}
