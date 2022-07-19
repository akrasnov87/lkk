package ru.mobnius.cic.data.storage;

import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;

import java.util.Map;

import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.models.Audits;
import ru.mobnius.simple_core.data.DbOperationType;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс для записи активности пользователя в БД
 */
public class AuditUtil {

    public final static String AUTHORIZED = "AUTHORIZED";
    public final static String UNAUTHORIZED = "UNAUTHORIZED";
    public final static String UPDATE_EXISTING_PHOTO = "UPDATE_EXISTING_PHOTO";
    public final static String DISABLE_PHOTO = "DISABLE_PHOTO";
    public final static String UPDATE_RESULT_NEW_PHOTO = "UPDATE_RESULT_NEW_PHOTO";
    public final static String FIRST_SAVE_PHOTOS = "FIRST_SAVE_PHOTOS";
    public final static String CREATE_RESULT = "CREATE_RESULT";
    public final static String UPDATE_RESULT = "UPDATE_RESULT";
    public final static String DISABLE_RESULT = "DISABLE_RESULT";
    public final static String TRAFFIC_OUTPUT = "TRAFFIC_OUTPUT";
    public final static String TRAFFIC_INPUT = "TRAFFIC_INPUT";

    public static void writeAudit(final @NonNull String type, final @NonNull String data, final @NonNull String version) {
        if (DataManager.getInstance() == null) {
            return;
        }
        final long userId;
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            userId = LongUtil.MINUS;

        } else {
            userId = Authorization.getInstance().user.getUserId();
        }
        final Audits audits = new Audits();
        audits.c_app_name = version;
        audits.fn_user = userId;
        audits.d_date = DateUtil.getNewDateStringForServer();
        audits.c_type = type;
        audits.c_data = data;
        audits.objectOperationType = DbOperationType.CREATED;
        try {
            DataManager.getInstance().daoSession.getAuditsDao().insert(audits);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    @NonNull
    public static String getMessageByTid(final @NonNull String tid,
                                         final @NonNull Map<String, String> tidMap) {
        if (tidMap.containsKey(tid)) {
            return StringUtil.defaultEmptyString(tidMap.get(tid));
        }
        return StringUtil.EMPTY;
    }

}
