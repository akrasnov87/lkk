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
 * Класс обработки обновления выполненного задания
 * в дополнительном потоке
 */
public class UpdateResultTask implements Callable<SavedResult> {

    @NonNull
    private final String routeId;
    @NonNull
    private final String pointId;
    @NonNull
    private final String resultId;
    @NonNull
    private final Location location;
    @NonNull
    private final Location pointLocation;
    @NonNull
    private final List<ImageItem> showingItems;
    @NonNull
    private final List<ImageItem> dbSavedImageItems;
    @NonNull
    private final String jbData;
    @NonNull
    private final String notice;
    @NonNull
    private final long mobileCauseId;

    public UpdateResultTask(final @NonNull String routeId,
                            final @NonNull String pointId,
                            final @NonNull String resultId,
                            final @NonNull Location location,
                            final @NonNull Location pointLocation,
                            final @NonNull String jbData,
                            final @NonNull List<ImageItem> showingItems,
                            final @NonNull List<ImageItem> dbSavedImageItems,
                            final @NonNull String notice,
                            final @NonNull long mobileCauseId) {
        this.pointId = pointId;
        this.routeId = routeId;
        this.resultId = resultId;
        this.location = location;
        this.showingItems = showingItems;
        this.dbSavedImageItems = dbSavedImageItems;
        this.jbData = jbData;
        this.pointLocation = pointLocation;
        this.mobileCauseId = mobileCauseId;
        this.notice = notice;
    }

    @NonNull
    @Override
    public SavedResult call() throws Exception {
        if (DataManager.getInstance() == null) {
            return new SavedResult(pointId, resultId, false, MobniusApplication.ERROR_NO_DB_CONNECTION);
        }
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return new SavedResult(pointId, resultId, false, MobniusApplication.ERROR_NO_AUTHORIZATION_DATA);
        }
        final long userId = Authorization.getInstance().user.getUserId();
        DataManager.getInstance().updateResult(resultId, location, pointLocation, jbData, notice, mobileCauseId);
        AuditUtil.writeAudit(AuditUtil.UPDATE_RESULT, resultId, StringUtil.EMPTY);
        int updated = 0;
        int saved = 0;
        for (final ImageItem showingItem : showingItems) {
            final int result = DataManager.getInstance().saveImage(showingItem, resultId, location, pointId, routeId, userId);
            if (result == DataManager.SAVE) {
                saved++;
                continue;
            }
            if (result == DataManager.UPDATE) {
                updated++;
            }
        }
        if (saved > 0) {
            AuditUtil.writeAudit(AuditUtil.UPDATE_RESULT_NEW_PHOTO, BaseApp.SAVED + saved, StringUtil.EMPTY);
        }
        if (updated > 0) {
            AuditUtil.writeAudit(AuditUtil.UPDATE_EXISTING_PHOTO, BaseApp.UPDATED + updated, StringUtil.EMPTY);
        }
        int disabled = 0;
        for (final ImageItem dbItem : dbSavedImageItems) {
            boolean notContains = true;
            for (final ImageItem showingItem : showingItems) {
                if (StringUtil.equals(dbItem.id, showingItem.id)) {
                    notContains = false;
                    break;
                }
            }
            if (notContains) {
                DataManager.getInstance().disableImage(dbItem.id, dbItem.bitmap.getByteCount());
                disabled++;
            }
        }
        if (disabled > 0) {
            AuditUtil.writeAudit(AuditUtil.DISABLE_PHOTO, BaseApp.DISABLED + disabled, StringUtil.EMPTY);
        }
        return new SavedResult(pointId, resultId, true, MobniusApplication.TASK_SAVED_SUCCESSFULLY);
    }
}
