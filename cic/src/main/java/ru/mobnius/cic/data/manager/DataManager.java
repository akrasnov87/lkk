package ru.mobnius.cic.data.manager;

import static ru.mobnius.cic.MobniusApplication.USER_ENG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.data.search.AndPointFilter;
import ru.mobnius.cic.data.search.AndRouteFilter;
import ru.mobnius.cic.data.search.OrPointFilter;
import ru.mobnius.cic.data.search.OrRouteFilter;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.cic.data.storage.AuditUtil;
import ru.mobnius.cic.data.storage.DbOpenHelper;
import ru.mobnius.cic.data.storage.Queries;
import ru.mobnius.cic.data.storage.models.AttachmentTypes;
import ru.mobnius.cic.data.storage.models.AttachmentTypesDao;
import ru.mobnius.cic.data.storage.models.Attachments;
import ru.mobnius.cic.data.storage.models.AttachmentsDao;
import ru.mobnius.cic.data.storage.models.Causes;
import ru.mobnius.cic.data.storage.models.CausesDao;
import ru.mobnius.cic.data.storage.models.DaoMaster;
import ru.mobnius.cic.data.storage.models.DaoSession;
import ru.mobnius.cic.data.storage.models.Points;
import ru.mobnius.cic.data.storage.models.PointsDao;
import ru.mobnius.cic.data.storage.models.ResultHistory;
import ru.mobnius.cic.data.storage.models.ResultHistoryDao;
import ru.mobnius.cic.data.storage.models.ResultStatuses;
import ru.mobnius.cic.data.storage.models.Results;
import ru.mobnius.cic.data.storage.models.ResultsDao;
import ru.mobnius.cic.data.storage.models.RouteHistory;
import ru.mobnius.cic.data.storage.models.RouteHistoryDao;
import ru.mobnius.cic.data.storage.models.RouteStatuses;
import ru.mobnius.cic.data.storage.models.RouteStatusesDao;
import ru.mobnius.cic.data.storage.models.Routes;
import ru.mobnius.cic.data.storage.models.Users;
import ru.mobnius.cic.ui.component.ExpandableTextLayout;
import ru.mobnius.cic.ui.component.SimpleExpandableItem;
import ru.mobnius.cic.ui.model.FailureImageObject;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.cic.ui.model.MeterItem;
import ru.mobnius.cic.ui.model.PointCountInfo;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.PointState;
import ru.mobnius.cic.ui.model.ProfileItem;
import ru.mobnius.cic.ui.model.ResultHistoryItem;
import ru.mobnius.cic.ui.model.ResultItem;
import ru.mobnius.cic.ui.model.RouteInfoHistory;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.cic.ui.model.SearchHeader;
import ru.mobnius.simple_core.data.DbOperationType;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.DoubleUtil;
import ru.mobnius.simple_core.utils.IntUtil;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.NetworkInfoUtil;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Вспомогательный класс с функциями для получения/изменения данных в БД Sqlite
 */
public class DataManager {
    public static final String FAILURE_CONST_NO_ACCESS = "NO_ACCESS";
    public static final String FAILURE_CONST_MECHANICAL_DAMAGE = "MECHANICAL_DAMAGE";
    public static final String FAILURE_CONST_PU_REPLACE = "DEVICE_REPLACE";
    public static final String FAILURE_CONST_SEAL_DAMAGE = "SEAL_DAMAGE";
    public static final String FAILURE_CONST_TYPE_MISSMATCH = "TYPE_MISSMATCH";
    public static final String FAILURE_CONST_NUMBER_MISSMATCH = "NUMBER_MISSMATCH";
    public static final String FAILURE_CONST_PU_DISCONNECTED = "DEVICE_DISCONNECTED";

    public static final String FAILURE_CONST_PU_MISSING = "DEVICE_MISSING";
    public static final String FAILURE_CONST_POWER_OBJ_LIQUIDATED = "LIQUIDATED";
    public static final String FAILURE_CONST_CONSUMER_BLOCK = "CONSUMER_BLOCK";

    public static final String METER_PHOTO_CONST = "METER";
    public static final String SEAL_PHOTO_CONST = "SEAL";
    public static final String DEVICE_PHOTO_CONST = "DEVICE";
    public static final String OVERVIEW_PHOTO_CONST = "OVERVIEW";
    public static final String FAILURE_PHOTO_CONST = "FAILURE";

    public final static int NONE = -1;
    public final static int UPDATE = 0;
    public final static int DELETE = 1;
    public final static int SAVE = 2;


    @NonNull
    public final DaoSession daoSession;
    @Nullable
    private static DataManager dataManager;


    public static DataManager createInstance(final @NonNull DaoSession daoSession) {
        return dataManager = new DataManager(daoSession);
    }

    @Nullable
    public static DataManager getInstance() {
        return dataManager;
    }

    private DataManager(final @NonNull DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public int saveImage(final @NonNull ImageItem imageItem,
                         final @NonNull String resultId,
                         final @NonNull Location location,
                         final @NonNull String pointId,
                         final @NonNull String routeId,
                         final long userId) {
        if (imageItem.isPlaceholder()) {
            return NONE;
        }
        final Attachments loaded = daoSession.getAttachmentsDao().load(imageItem.id);
        if (loaded != null) {
            if (StringUtil.notEquals(loaded.c_notice, imageItem.imageType.notice)
                    || loaded.fn_type != imageItem.imageType.typeId) {
                updateImage(imageItem.id, imageItem.imageType.notice, imageItem.imageType.typeId);
            }
            return UPDATE;
        }
        final Attachments attachment = new Attachments();
        attachment.c_path = imageItem.fileName;
        attachment.c_real_file_path = imageItem.absFilePath;
        attachment.fn_type = imageItem.imageType.typeId;
        attachment.fn_result = resultId;
        attachment.n_latitude = location.getLatitude();
        attachment.n_longitude = location.getLongitude();
        attachment.d_date = DateUtil.getNewDateStringForServer();
        attachment.fn_point = pointId;
        attachment.fn_route = routeId;
        attachment.fn_user = userId;
        attachment.n_size = imageItem.bitmap.getByteCount();
        attachment.c_notice = imageItem.imageType.notice;
        attachment.dx_created = DateUtil.getNewDateStringForServer();
        attachment.objectOperationType = DbOperationType.CREATED;
        attachment.c_mime = "image/jpeg";

        daoSession.getAttachmentsDao().insert(attachment);
        return SAVE;
    }

    @NonNull
    public String createResult(final @NonNull String pointId,
                               final @NonNull String routeId,
                               final long userId,
                               final @Nullable String jData,
                               final @NonNull Location userLocation,
                               final @NonNull Location pointLocation,
                               final @NonNull String notice,
                               final long failureReasonId) {
        final Results result = new Results();
        result.id = UUID.randomUUID().toString();
        result.fn_user = userId;
        result.d_date = DateUtil.getNewDateStringForServer();
        result.fn_point = pointId;
        result.fn_route = routeId;
        result.b_disabled = false;
        if (StringUtil.isNotEmpty(jData)) {
            result.jb_data = jData;
        }
        result.__b_reject = false;
        result.n_latitude = userLocation.getLatitude();
        result.n_longitude = userLocation.getLongitude();
        result.n_distance = (int) userLocation.distanceTo(pointLocation);
        result.c_notice = notice;
        result.isSynchronization = false;
        result.objectOperationType = DbOperationType.CREATED;
        daoSession.getResultsDao().insert(result);

        final ResultHistory resultHistory = new ResultHistory();
        resultHistory.id = UUID.randomUUID().toString();
        resultHistory.fn_route = routeId;
        resultHistory.fn_result = result.id;
        resultHistory.fn_status = getNotConfirmedResultStatus();
        resultHistory.fn_cause = failureReasonId;
        resultHistory.fn_user = userId;
        resultHistory.objectOperationType = DbOperationType.CREATED;
        resultHistory.d_date = DateUtil.getNewDateStringForServer();
        daoSession.getResultHistoryDao().insert(resultHistory);

        assignStatusIfNotAlreadyAssigned(routeId, RouteItem.ROUTE_STATUS_IN_PROCESS);
        return result.id;
    }

    private long getNotConfirmedResultStatus() {
        final List<ResultStatuses> statuses = daoSession.getResultStatusesDao().loadAll();
        if (statuses == null) {
            return LongUtil.MINUS;
        }
        for (final ResultStatuses status : statuses) {
            if (StringUtil.equalsIgnoreCase(status.c_const, ResultItem.RESULT_STATUS_CREATED_CONST)) {
                return LongUtil.getLongOrMinus(status.id);
            }
        }
        return LongUtil.MINUS;
    }

    public void updateResult(final @NonNull String resultId,
                             final @NonNull Location userLocation,
                             final @NonNull Location pointLocation,
                             final @Nullable String jData,
                             final @Nullable String notice,
                             final long failureReasonId) {
        final Results result = daoSession.getResultsDao().load(resultId);
        if (result == null) {
            return;
        }
        if (StringUtil.isNotEmpty(jData)) {
            result.jb_data = jData;
        }
        result.n_latitude = userLocation.getLatitude();
        result.n_longitude = userLocation.getLatitude();
        result.n_distance = (int) userLocation.distanceTo(pointLocation);
        result.c_notice = StringUtil.defaultEmptyString(notice);
        result.__b_reject = false;
        if (result.objectOperationType == null ||
                !result.objectOperationType.equals(DbOperationType.CREATED)) {
            result.objectOperationType = DbOperationType.UPDATED;
            result.isSynchronization = false;
        }
        daoSession.getResultsDao().update(result);

        final ResultHistory resultHistory = new ResultHistory();
        resultHistory.id = UUID.randomUUID().toString();
        resultHistory.fn_route = result.fn_route;
        resultHistory.fn_result = result.id;
        resultHistory.fn_status = getNotConfirmedResultStatus();
        resultHistory.fn_cause = failureReasonId;
        resultHistory.objectOperationType = DbOperationType.CREATED;
        resultHistory.fn_user = result.fn_user;
        resultHistory.d_date = DateUtil.getNewDateStringForServer();
        daoSession.getResultHistoryDao().insert(resultHistory);

    }

    /**
     * обновление существующего файла
     *
     * @param attachmentId идентификатор файла
     * @param type         тип
     * @param notice       описание
     */
    public void updateImage(final @NonNull String attachmentId,
                            final @NonNull String notice,
                            final long type) {
        final Attachments attachment = daoSession.getAttachmentsDao().load(attachmentId);
        if (attachment == null) {
            return;
        }
        attachment.fn_type = type;
        attachment.c_notice = notice;
        if (!attachment.objectOperationType.equals(DbOperationType.CREATED) && !attachment.isSynchronization) {
            attachment.objectOperationType = DbOperationType.UPDATED;
            attachment.isSynchronization = false;
        }
        daoSession.getAttachmentsDao().update(attachment);
    }

    /**
     * Получение всех маршрутов.
     *
     * @return Список маршрутов
     */
    @NonNull
    public List<RouteItem> getAllRouteItems() {
        final List<Routes> routes = daoSession.getRoutesDao().loadAll();
        final List<RouteItem> routeItems = new ArrayList<>();
        if (routes != null) {
            for (Routes route : routes) {
                if (StringUtil.isEmpty(route.id)) {
                    continue;
                }
                final String notice = StringUtil.defaultEmptyString(route.c_notice);
                final String name = StringUtil.defaultEmptyString(route.c_name);
                final Date dateStart = DateUtil.getNonNullDateFromServerString(route.d_date_start);
                final Date dateEnd = DateUtil.getNonNullDateFromServerString(route.d_date_end);
                Date extended = null;
                if (route.b_extended && route.d_extended != null) {
                    extended = DateUtil.getNullableDateFromServerString(route.d_extended);
                }
                final PointCountInfo pointsCountInfo = getPointCountInfo(route.id);
                final RouteItem routeItem = new RouteItem(route.id, notice, name,
                        dateStart, dateEnd, route.b_extended, extended,
                        route.n_order, pointsCountInfo.totalCount, pointsCountInfo.doneCount, route.b_draft);

                routeItems.add(routeItem);
            }
            return routeItems;
        }
        return routeItems;
    }

    /**
     * Альтернативный способ получения точек маршрута
     *
     * @param routeId идентификатор маршрута. Можно передать null
     * @return список точек
     */
    @NonNull
    public List<PointItem> getRoutePointItems(final @NonNull String routeId) {
        final List<PointItem> pointItems = new ArrayList<>();
        try (final Cursor cursor = daoSession.getDatabase().rawQuery(
                Queries.POINT_ITEM_QUERY, new String[]{routeId})) {
            if (cursor.moveToFirst()) {
                final int maxCount = cursor.getColumnCount();
                if (maxCount != 12) {
                    return getNoCursorRoutePointItems(routeId);
                }
                do {
                    final String id = cursor.getString(0);
                    final String f_route = cursor.getString(1);
                    if (StringUtil.isEmpty(id) || StringUtil.isEmpty(f_route)) {
                        continue;
                    }
                    final String f_result_id = cursor.getString(2);
                    final String c_address = cursor.getString(3);
                    final String jb_data = cursor.getString(4);
                    final boolean b_person = cursor.getInt(5) == 1;
                    final boolean b_done = cursor.getInt(6) == 1;
                    final boolean b_sync = cursor.getInt(7) == 1;
                    final boolean b_reject = cursor.getInt(8) == 1;
                    final double n_latitude = DoubleUtil.getDoubleOrZero(cursor.getDouble(9));
                    final double n_longitude = DoubleUtil.getDoubleOrZero(cursor.getDouble(10));
                    final int n_order = IntUtil.getIntOrZero(cursor.getInt(11));
                    final PointItem pointItem = new PointItem(id,
                            f_route,
                            f_result_id,
                            c_address,
                            jb_data,
                            b_person,
                            b_done,
                            b_sync,
                            b_reject,
                            n_latitude,
                            n_longitude,
                            n_order);
                    pointItems.add(pointItem);
                } while (cursor.moveToNext());
                if (pointItems.size() > 0) {
                    return pointItems;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getNoCursorRoutePointItems(routeId);
    }

    @NonNull
    private List<PointItem> getNoCursorRoutePointItems(final @NonNull String routeId) {
        final List<Points> points = daoSession.getPointsDao().queryBuilder().
                where(PointsDao.Properties.F_route.eq(routeId)).
                orderAsc(PointsDao.Properties.N_order).build().list();
        final List<PointItem> pointItems = new ArrayList<>();
        if (points == null || points.size() == 0) {
            return pointItems;
        }
        for (final Points point : points) {
            final String resultId = getNullableResultId(point.id);
            final PointState pointState = getPointState(point.id);
            final double latitude = DoubleUtil.getDoubleOrZero(point.n_latitude);
            final double longitude = DoubleUtil.getDoubleOrZero(point.n_longitude);
            final PointItem pointItem = new PointItem(point.id,
                    point.f_route,
                    resultId,
                    point.c_address,
                    point.jb_data,
                    point.b_person,
                    pointState.isDone,
                    pointState.isSync,
                    pointState.isReject,
                    latitude,
                    longitude,
                    point.n_order);
            pointItems.add(pointItem);
        }
        return pointItems;
    }

    /**
     * Альтернативный способ получения точек маршрута
     *
     * @return список точек
     */
    @NonNull
    public List<PointItem> getAllPointItems() {

        final List<Points> points = daoSession.getPointsDao().loadAll();
        final List<PointItem> pointItems = new ArrayList<>();
        if (points == null || points.size() == 0) {
            return pointItems;
        }
        for (final Points point : points) {
            final String resultId = getNullableResultId(point.id);
            final PointState pointState = getPointState(point.id);
            final double latitude = DoubleUtil.getDoubleOrZero(point.n_latitude);
            final double longitude = DoubleUtil.getDoubleOrZero(point.n_longitude);
            final PointItem pointItem = new PointItem(point.id,
                    point.f_route,
                    resultId,
                    point.c_address,
                    point.jb_data,
                    point.b_person,
                    pointState.isDone,
                    pointState.isSync,
                    pointState.isReject,
                    latitude,
                    longitude,
                    point.n_order);
            pointItems.add(pointItem);
        }

        return pointItems;

    }


    @Nullable
    public String getNullableResultId(final @NonNull String pointId) {
        try (final Cursor cursor = daoSession.getDatabase().rawQuery(
                Queries.RESUL_ID_QUERY, new String[]{pointId})) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Вернуть состояние точки маршрута
     *
     * @param pointId идентификатор точки маршрута
     * @return состояние
     */
    @NonNull
    public PointState getPointState(final @NonNull String pointId) {
        try (final Cursor cursor = daoSession.getDatabase().rawQuery(
                Queries.POINT_STATE_QUERY, new String[]{pointId})) {
            if (cursor.moveToFirst()) {
                final PointState pointState = new PointState();
                pointState.isDone = true;
                pointState.isSync = cursor.getLong(0) == 1L;
                pointState.isReject = cursor.getLong(1) == 1L;
                return pointState;
            }
            return new PointState();
        } catch (Exception e) {
            e.printStackTrace();
            return new PointState();
        }
    }

    @NonNull
    public List<RouteInfoHistory> getRouteHistory(final @NonNull String routeId) {
        final List<RouteInfoHistory> historyList = new ArrayList<>();
        try (final Cursor cursor = daoSession.getDatabase()
                .rawQuery(Queries.ROUTE_HISTORY_QUERY, new String[]{routeId})) {

            if (cursor.moveToFirst()) {
                do { // переходим на первую строку
                    final String c_name = cursor.getString(0);
                    final String d_date = cursor.getString(1);
                    if (StringUtil.isEmpty(c_name) || d_date == null) {
                        continue;
                    }
                    final RouteInfoHistory history = new RouteInfoHistory(c_name, DateUtil.getNonNullDateFromServerString(d_date));
                    historyList.add(history);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyList;
    }

    /**
     * Функция для получения списка типов изображений
     *
     * @return список типов изображений
     */
    @NonNull
    public List<ImageType> loadPhotoTypes() {
        final List<AttachmentTypes> attachmentTypes = daoSession.getAttachmentTypesDao().queryBuilder().orderDesc(AttachmentTypesDao.Properties.N_order).list();
        if (attachmentTypes == null || attachmentTypes.size() == 0) {
            return new ArrayList<>();
        }
        final List<ImageType> imageTypes = new ArrayList<>();
        for (final AttachmentTypes attachmentType : attachmentTypes) {
            final ImageType type = new ImageType(LongUtil.getLongOrMinus(attachmentType.id),
                    StringUtil.defaultEmptyString(attachmentType.c_name),
                    StringUtil.defaultEmptyString(attachmentType.c_const), StringUtil.EMPTY, attachmentType.b_default);
            imageTypes.add(type);
        }
        return imageTypes;
    }

    public @Nullable
    ProfileItem getProfile() {
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return null;
        }
        Users user = daoSession.getUsersDao().load(Authorization.getInstance().user.getUserId());
        if (user != null) {
            final String fio = StringUtil.isEmpty(user.c_fio) ? user.c_login : user.c_fio;
            return new ProfileItem(StringUtil.defaultEmptyString(fio));
        }
        return null;
    }

    /**
     * Количество не синхронизированных результатов
     *
     * @return количество результатов
     */
    public int getUnSyncCount() {
        final List<Results> results = daoSession
                .getResultsDao()
                .queryBuilder()
                .where(ResultsDao.Properties.IsSynchronization.eq(false), ResultsDao.Properties.__b_reject.eq(false), ResultsDao.Properties.B_disabled.eq(false))
                .list();
        final Set<String> set = new HashSet<>();
        for (Results result : results) {
            set.add(result.fn_point);
        }
        return set.size();
    }

    public boolean isCurrentRoute(String routeId) {
        Routes routes = daoSession.getRoutesDao().load(routeId);

        Date dateStart = DateUtil.getNullableDateFromServerString(routes.d_date_start);
        Date dateEnd = DateUtil.getNullableDateFromServerString(routes.d_date_end);
        Date date = new Date();
        if (dateEnd == null || dateStart == null) {
            return false;
        }
        return dateStart.getTime() < date.getTime() &&
                dateEnd.getTime() + DateUtil.ONE_DAY_TIME > date.getTime();

    }

    public void updateUser(Long userId, String login, String userName) {
        Users user = daoSession.getUsersDao().load(userId);
        if (user == null) {
            Users newUser = new Users();
            newUser.id = userId;
            newUser.c_login = login;
            newUser.c_fio = userName;

            daoSession.getUsersDao().insert(newUser);
        }
    }

    @NonNull
    public List<ResultItem> getResultItems(final @NonNull String pointId) {
        final List<ResultItem> items = new ArrayList<>();
        final List<Results> results = daoSession.getResultsDao().queryBuilder().where(ResultsDao.Properties.Fn_point.eq(pointId)).build().list();
        if (results == null) {
            return items;
        }
        for (final Results result : results) {
            if (StringUtil.isNotEmpty(result.id)) {
                final ResultItem item = new ResultItem(result.id,
                        DateUtil.getNonNullShortDateText(result.d_date),
                        result.jb_data,
                        result.isSynchronization,
                        result.b_disabled);
                items.add(item);
            }
        }
        return items;
    }

    @NonNull
    public List<ImageItem> loadImageItems(final @NonNull Context context, final @NonNull String resultId) {
        final List<ImageItem> imageItems = new ArrayList<>();
        final List<Attachments> attachments = daoSession.getAttachmentsDao()
                .queryBuilder()
                .where(AttachmentsDao.Properties.Fn_result.eq(resultId), AttachmentsDao.Properties.B_disabled.eq(false))
                .build()
                .list();
        if (attachments == null) {
            return imageItems;
        }
        for (final Attachments attachment : attachments) {
            final AttachmentTypes type = attachment.getType();
            if (type == null) {
                continue;
            }
            final String fileName = attachment.c_path;
            final String filePath = attachment.c_real_file_path;
            if (StringUtil.isEmpty(filePath) && StringUtil.isEmpty(fileName)) {
                continue;
            }
            final String id = attachment.id;
            final Bitmap bitmap;
            if (attachment.isSynchronization) {
                if (!NetworkInfoUtil.isNetworkAvailable(context)) {
                    Toast.makeText(context, context.getString(R.string.need_network_to_load_photo), Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    final String url = GlobalSettings.getConnectUrl() + "/file/attachment/" + attachment.id;
                    bitmap = Glide.with(context).asBitmap().load(url).submit().get(3, TimeUnit.SECONDS);
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                    continue;
                }
            } else {
                if (StringUtil.isEmpty(filePath)) {
                    continue;
                }
                bitmap = BitmapFactory.decodeFile(filePath);
            }
            final Date date = DateUtil.getNonNullDateFromServerString(attachment.d_date);
            final Long typeId = type.id;
            final String typeName = type.c_name;
            final String typeConst = type.c_const;
            final String notice = attachment.c_notice;
            final boolean isDefaultType = type.b_default;
            final Location location = new Location(MobniusApplication.APP_NAME);
            location.setLatitude(attachment.n_latitude);
            location.setLongitude(attachment.n_longitude);
            if (checkNonNull(id, fileName, bitmap, date, typeId, typeConst, typeName)) {
                final ImageType imageType = new ImageType(LongUtil.getLongOrMinus(typeId), typeName, typeConst, StringUtil.defaultEmptyString(notice), isDefaultType);
                final ImageItem imageItem = new ImageItem(id, fileName, filePath, date, bitmap, imageType, location, true);
                imageItems.add(imageItem);
            }
        }
        return imageItems;
    }

    @NonNull
    public List<String> loadSavedImagesPaths(final @NonNull String resultId) {
        final List<String> savedImagePaths = new ArrayList<>();
        final List<Attachments> attachments = daoSession.getAttachmentsDao().queryBuilder().where(AttachmentsDao.Properties.Fn_result.eq(resultId)).build().list();
        if (attachments == null) {
            return savedImagePaths;
        }
        for (final Attachments attachment : attachments) {
            final String filePath = attachment.c_real_file_path;
            if (StringUtil.isNotEmpty(filePath)) {
                savedImagePaths.add(filePath);
            }
        }
        return savedImagePaths;
    }

    /**
     * Получение списка показаний
     *
     * @param pointId точка маршрута
     * @return список показаний
     */
    @NonNull
    public List<MeterItem> getInputMeters(final @NonNull String pointId) {
        final PointItem pointItem = getPointItem(pointId);
        if (pointItem == null) {
            return new ArrayList<>();
        }
        final List<MeterItem> pointMeterItems = pointItem.getMeters();
        if (StringUtil.isEmpty(pointItem.resultId)) {
            return pointMeterItems;
        }
        final ResultItem resultItem = getResultItem(pointItem.resultId);
        if (resultItem == null) {
            return pointMeterItems;
        }
        final List<MeterItem> resultMeterItems = resultItem.getResultMeters();
        for (final MeterItem pointMeterItem : pointMeterItems) {
            for (final MeterItem resultMeterItem : resultMeterItems) {
                if (StringUtil.equals(pointMeterItem.id, resultMeterItem.id)) {
                    pointMeterItem.currentDate = resultMeterItem.currentDate;
                    pointMeterItem.setCurrentValue(resultMeterItem.getCurrentValue());
                    pointMeterItem.startValue = resultMeterItem.startValue;
                }
            }
        }
        return pointMeterItems;
    }

    public boolean getIsReceipt(final @NonNull String resultId) {
        final ResultItem resultItem = getResultItem(resultId);
        if (resultItem == null) {
            return false;
        }
        return resultItem.isReceipt;
    }

    public boolean getIsNotificationOpr(final @NonNull String resultId) {
        final ResultItem resultItem = getResultItem(resultId);
        if (resultItem == null) {
            return false;
        }
        return resultItem.isNotificationOrp;
    }

    @NonNull
    public List<FailureImageObject> getFailureImageDictionary(final @NonNull String pointId) {
        int metersCount = getInputMetersCount(pointId);
        final List<FailureImageObject> failureImageDictionary = new ArrayList<>();
        final List<AttachmentTypes> attachmentTypes = daoSession
                .getAttachmentTypesDao()
                .loadAll();

        final List<Causes> mobileCauses = daoSession
                .getCausesDao()
                .queryBuilder()
                .where(CausesDao.Properties.B_mobile.eq(true))
                .list();

        if (attachmentTypes == null
                || mobileCauses == null
                || attachmentTypes.isEmpty()
                || mobileCauses.isEmpty()) {
            return failureImageDictionary;
        }

        //заполняем значениями по умолчанию для случаев когда тип
        //причины невыполнения не выбран исключая обзорное фото и фото сбойного ПУ
        for (final AttachmentTypes attachmentType : attachmentTypes) {
            if (StringUtil.notEquals(attachmentType.c_const, OVERVIEW_PHOTO_CONST) &&
                    StringUtil.notEquals(attachmentType.c_const, FAILURE_PHOTO_CONST)) {
                failureImageDictionary
                        .add(new FailureImageObject(LongUtil.MINUS,
                                attachmentType.id,
                                getRequiredImageCount(attachmentType.c_const, metersCount),
                                StringUtil.defaultEmptyString(attachmentType.c_name)));
            }
        }

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_CONSUMER_BLOCK, mobileCauses),
                metersCount,
                OVERVIEW_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_NO_ACCESS, mobileCauses),
                metersCount,
                OVERVIEW_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_POWER_OBJ_LIQUIDATED, mobileCauses),
                metersCount,
                OVERVIEW_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_PU_MISSING, mobileCauses),
                metersCount,
                OVERVIEW_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_MECHANICAL_DAMAGE, mobileCauses),
                metersCount,
                FAILURE_PHOTO_CONST, METER_PHOTO_CONST, SEAL_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_PU_REPLACE, mobileCauses),
                metersCount,
                DEVICE_PHOTO_CONST, METER_PHOTO_CONST, SEAL_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_SEAL_DAMAGE, mobileCauses),
                metersCount,
                DEVICE_PHOTO_CONST, METER_PHOTO_CONST, SEAL_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_TYPE_MISSMATCH, mobileCauses),
                metersCount,
                DEVICE_PHOTO_CONST, METER_PHOTO_CONST, SEAL_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_NUMBER_MISSMATCH, mobileCauses),
                metersCount,
                DEVICE_PHOTO_CONST, METER_PHOTO_CONST, SEAL_PHOTO_CONST);

        addAttachmentTypeByConst(attachmentTypes,
                failureImageDictionary,
                getMobileFailureReasonByConst(FAILURE_CONST_PU_DISCONNECTED, mobileCauses),
                metersCount,
                DEVICE_PHOTO_CONST, METER_PHOTO_CONST, SEAL_PHOTO_CONST);
        return failureImageDictionary;
    }

    private long getMobileFailureReasonByConst(final @NonNull String failureConst,
                                               final @NonNull List<Causes> mobileCauses) {
        for (final Causes cause : mobileCauses) {
            if (StringUtil.equalsIgnoreCase(cause.c_const, failureConst)) {
                return cause.id;
            }
        }
        return LongUtil.MINUS;
    }

    private void addAttachmentTypeByConst(final @NonNull List<AttachmentTypes> attachmentTypes,
                                          final @NonNull List<FailureImageObject> failureImageObject,
                                          final long failureId,
                                          final int metersCount,
                                          final @NonNull String... consts) {
        if (failureId == LongUtil.MINUS) {
            return;
        }
        for (final String attachmentConst : consts) {
            for (final AttachmentTypes attachmentType : attachmentTypes) {
                if (StringUtil.equalsIgnoreCase(attachmentType.c_const, attachmentConst)) {
                    failureImageObject.add(new FailureImageObject(failureId,
                            attachmentType.id,
                            getRequiredImageCount(attachmentType.c_const, metersCount),
                            StringUtil.defaultEmptyString(attachmentType.c_name)));
                }
            }
        }
    }

    private int getRequiredImageCount(final @NonNull String attachmentTypeConst, int meterCount) {
        if (StringUtil.equalsIgnoreCase(DEVICE_PHOTO_CONST, attachmentTypeConst)) {
            return meterCount;
        }
        return 1;
    }

    private boolean checkNonNull(final Object... objects) {
        for (final Object o : objects) {
            if (o == null) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private PointItem getPointItem(final @NonNull String pointId) {
        final Points point = daoSession.getPointsDao().load(pointId);
        if (point == null) {
            return null;
        }
        final String resultId = getNullableResultId(point.id);
        final PointState pointState = getPointState(point.id);
        final double latitude = DoubleUtil.getDoubleOrZero(point.n_latitude);
        final double longitude = DoubleUtil.getDoubleOrZero(point.n_longitude);
        return new PointItem(point.id,
                point.f_route,
                resultId,
                point.c_address,
                point.jb_data,
                point.b_person,
                pointState.isDone,
                pointState.isSync,
                pointState.isReject,
                latitude,
                longitude,
                point.n_order);
    }

    @Nullable
    private ResultItem getResultItem(final @NonNull String resultId) {
        final Results result = daoSession.getResultsDao().load(resultId);
        if (result == null) {
            return null;
        }
        return new ResultItem(result.id, result.d_date, result.jb_data, result.isSynchronization, result.b_disabled);
    }


    @Nullable
    public RouteItem getRouteItem(final @NonNull String routeId) {
        final Routes route = daoSession.getRoutesDao().load(routeId);
        if (route == null) {
            return null;
        }
        if (StringUtil.isEmpty(route.id)) {
            return null;
        }
        final String notice = StringUtil.defaultEmptyString(route.c_notice);
        final String name = StringUtil.defaultEmptyString(route.c_name);
        final Date dateStart = DateUtil.getNonNullDateFromServerString(route.d_date_start);
        final Date dateEnd = DateUtil.getNonNullDateFromServerString(route.d_date_end);
        Date extended = null;
        if (route.b_extended && route.d_extended != null) {
            extended = DateUtil.getNullableDateFromServerString(route.d_extended);
        }
        final PointCountInfo pointsCountInfo = getPointCountInfo(route.id);

        return new RouteItem(route.id, notice, name,
                dateStart, dateEnd, route.b_extended, extended,
                route.n_order, pointsCountInfo.totalCount, pointsCountInfo.doneCount, route.b_draft);
    }

    @NonNull
    private PointCountInfo getPointCountInfo(final @NonNull String routeId) {
        try (final Cursor cursor = daoSession.getDatabase()
                .rawQuery(Queries.POINT_COUNT_QUERY, new String[]{routeId, routeId, routeId})) {
            if (cursor.moveToFirst()) {
                final long totalCount = cursor.getLong(0);
                final long doneCount = cursor.getLong(1);
                final long syncCount = cursor.getLong(2);
                return new PointCountInfo((int) totalCount, (int) doneCount, (int) syncCount);
            }
            return new PointCountInfo(0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new PointCountInfo(0, 0, 0);
        }
    }

    @NonNull
    public List<SearchResult> getSearchResults(final @NonNull String searchQuery,
                                               final @NonNull OrPointFilter orPointFilter,
                                               final @NonNull AndPointFilter andPointFilter,
                                               final @NonNull OrRouteFilter orRouteFilter,
                                               final @NonNull AndRouteFilter andRouteFilter,
                                               final int mode) {

        final List<SearchResult> searchResults = new ArrayList<>();
        if (mode == SearchResult.POINT_ONLY_MODE || mode == SearchResult.ALL_MODE) {
            List<PointItem> pointItems = orPointFilter.satisfiesRequirements(getAllPointItems(), searchQuery);
            pointItems = andPointFilter.satisfiesRequirements(pointItems, searchQuery);
            if (pointItems.size() > 0) {
                final SearchHeader pointHeader = new SearchHeader(MobniusApplication.POINTS, SearchHeader.PROFILE_TYPE_POINT_HEADER);
                searchResults.add(pointHeader);
                searchResults.addAll(pointItems);
            }
            if (mode == SearchResult.POINT_ONLY_MODE) {
                return searchResults;
            }
        }
        if (mode == SearchResult.ROUTE_ONLY_MODE || mode == SearchResult.ALL_MODE) {
            List<RouteItem> routeItems = orRouteFilter.satisfiesRequirements(getAllRouteItems(), searchQuery);
            routeItems = andRouteFilter.satisfiesRequirements(routeItems, searchQuery);
            if (routeItems.size() > 0) {
                final SearchHeader routesHeader = new SearchHeader(MobniusApplication.ROUTES, SearchHeader.PROFILE_TYPE_ROUTE_HEADER);
                searchResults.add(routesHeader);
                searchResults.addAll(routeItems);
            }
            if (mode == SearchResult.ROUTE_ONLY_MODE) {
                return searchResults;
            }
        }
        return searchResults;
    }

    public void destroy() {
        daoSession.clear();
    }

    public boolean cancelResult(final @NonNull String resultId) {
        try {
            final Results result = daoSession.getResultsDao().load(resultId);
            if (result == null) {
                return false;
            }
            result.b_disabled = true;
            daoSession.getResultsDao().update(result);
            final List<Attachments> attachments = daoSession.getAttachmentsDao().queryBuilder().where(AttachmentsDao.Properties.Fn_result.eq(resultId)).list();
            if (attachments != null) {
                for (final Attachments attachment : attachments) {
                    disableImage(attachment.id, attachment.n_size);
                }
            }
            AuditUtil.writeAudit(AuditUtil.DISABLE_RESULT, resultId, StringUtil.EMPTY);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public boolean isOverdueRoute(final @NonNull String routeId) {
        Routes routes = daoSession.getRoutesDao().load(routeId);

        Date dateEnd = DateUtil.getNullableDateFromServerString(routes.d_date_end);
        Date date = new Date();
        if (dateEnd == null) {
            return false;
        }
        return dateEnd.getTime() + DateUtil.ONE_DAY_TIME < date.getTime();
    }

    @NonNull
    public List<Causes> getMobileCauses() {
        final List<Causes> causes =
                daoSession.getCausesDao().queryBuilder().where(CausesDao.Properties.B_mobile.eq(true)).list();
        if (causes == null) {
            return new ArrayList<>();
        }
        return causes;
    }


    public void assignStatusIfNotAlreadyAssigned(final @NonNull String routeId, final @NonNull String status) {
        final List<RouteStatuses> routeStatuses = daoSession.getRouteStatusesDao()
                .queryBuilder().where(RouteStatusesDao.Properties.C_const.eq(status)).list();
        if (routeStatuses == null || routeStatuses.size() == 0) {
            return;
        }

        final long statusId = routeStatuses.get(0).id;
        final List<RouteHistory> routeHistories = daoSession.getRouteHistoryDao().queryBuilder()
                .where(RouteHistoryDao.Properties.Fn_route.eq(routeId)).list();

        if (routeHistories == null || routeHistories.size() == 0) {
            assignStatus(routeId, statusId);
            return;
        }
        for (final RouteHistory routeHistory : routeHistories) {
            if (routeHistory.fn_status == statusId) {
                return;
            }
        }
        assignStatus(routeId, statusId);
    }

    private void assignStatus(final @NonNull String routeId, final long statusId) {
        if (Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return;
        }
        final RouteHistory routeHistory = new RouteHistory();
        routeHistory.id = UUID.randomUUID().toString();
        routeHistory.c_notice = StringUtil.EMPTY;
        routeHistory.fn_status = statusId;
        routeHistory.fn_route = routeId;
        routeHistory.fn_user = Authorization.getInstance().user.getUserId();
        routeHistory.d_date = DateUtil.getNewDateStringForServer();
        routeHistory.objectOperationType = DbOperationType.CREATED;

        daoSession.getRouteHistoryDao().insert(routeHistory);
    }

    @Nullable
    public Causes getMobileFailureCause(final @NonNull String resultId) {
        final List<Causes> mobileCauses = daoSession.getCausesDao().queryBuilder().where(CausesDao.Properties.B_mobile.eq(true)).list();
        if (mobileCauses == null || mobileCauses.size() == 0) {
            return null;
        }
        final List<ResultHistory> resultHistories = daoSession
                .getResultHistoryDao()
                .queryBuilder()
                .where(ResultHistoryDao.Properties.Fn_result.eq(resultId))
                .list();
        if (resultHistories == null || resultHistories.size() == 0) {
            return null;
        }
        final List<ResultHistory> mobileFailureResultHistories = new ArrayList<>();
        for (final ResultHistory resultHistory : resultHistories) {
            for (Causes cause : mobileCauses) {
                if (resultHistory.fn_cause != null && resultHistory.fn_cause.equals(cause.id)) {
                    mobileFailureResultHistories.add(resultHistory);
                }
            }
        }
        if (mobileFailureResultHistories.size() == 0) {
            return null;
        }
        if (mobileFailureResultHistories.size() == 1) {
            return daoSession.getCausesDao().load(mobileFailureResultHistories.get(0).fn_cause);
        }
        ResultHistory lastResultHistory = mobileFailureResultHistories.get(0);
        Date lastResultHistoryDate = DateUtil.getNonNullSystemDate(lastResultHistory.d_date);
        for (int i = 1; i < mobileFailureResultHistories.size(); i++) {
            final ResultHistory mobileFailureResultHistory = mobileFailureResultHistories.get(i);
            final Date mobileResultHistoryDate = DateUtil.getNonNullSystemDate(mobileFailureResultHistory.d_date);
            if (mobileResultHistoryDate.after(lastResultHistoryDate)) {
                lastResultHistoryDate = mobileResultHistoryDate;
                lastResultHistory = mobileFailureResultHistory;
            }
        }
        return daoSession.getCausesDao().load(lastResultHistory.fn_cause);
    }

    @NonNull
    public String getServerFailureCause(final @NonNull String resultId) {
        final List<Causes> serverCauses = daoSession.getCausesDao().queryBuilder().where(CausesDao.Properties.B_mobile.eq(false)).list();
        if (serverCauses == null || serverCauses.size() == 0) {
            return StringUtil.EMPTY;
        }
        final List<ResultHistory> resultHistories = daoSession
                .getResultHistoryDao()
                .queryBuilder()
                .where(ResultHistoryDao.Properties.Fn_result.eq(resultId))
                .list();
        if (resultHistories == null || resultHistories.size() == 0) {
            return StringUtil.EMPTY;
        }
        final List<ResultHistory> serverFailureReasons = new ArrayList<>();
        for (final ResultHistory resultHistory : resultHistories) {
            for (Causes cause : serverCauses) {
                if (resultHistory.fn_cause != null && resultHistory.fn_cause.equals(cause.id)) {
                    serverFailureReasons.add(resultHistory);
                }
            }
        }
        if (serverFailureReasons.size() == 0) {
            return StringUtil.EMPTY;
        }
        if (serverFailureReasons.size() == 1) {
            final String serverCause = daoSession.getCausesDao().load(serverFailureReasons.get(0).fn_cause).c_name;
            return StringUtil.defaultEmptyString(serverCause);
        }
        ResultHistory lastResultHistory = serverFailureReasons.get(0);
        Date lastResultHistoryDate = DateUtil.getNonNullSystemDate(lastResultHistory.d_date);
        for (int i = 1; i < serverFailureReasons.size(); i++) {
            final ResultHistory mobileFailureResultHistory = serverFailureReasons.get(i);
            final Date mobileResultHistoryDate = DateUtil.getNonNullSystemDate(mobileFailureResultHistory.d_date);
            if (mobileResultHistoryDate.after(lastResultHistoryDate)) {
                lastResultHistoryDate = mobileResultHistoryDate;
                lastResultHistory = mobileFailureResultHistory;
            }
        }
        final String serverCause = daoSession.getCausesDao().load(lastResultHistory.fn_cause).c_name;
        return StringUtil.defaultEmptyString(serverCause);
    }

    public List<ExpandableTextLayout.OnExpandableItem> getResultHistoryList(final @NonNull String resultId) {
        final List<ExpandableTextLayout.OnExpandableItem> resultHistoryList = new ArrayList<>();
        final List<ResultHistoryItem> resultHistoryItemsList = new ArrayList<>();
        final List<ResultHistory> resultHistories = daoSession.getResultHistoryDao()
                .queryBuilder()
                .where(ResultHistoryDao.Properties.Fn_result.eq(resultId))
                .list();
        if (resultHistories == null || resultHistories.size() == 0) {
            return resultHistoryList;
        }
        final List<Causes> causes = daoSession.getCausesDao().loadAll();
        if (causes == null || causes.size() == 0) {
            return resultHistoryList;
        }
        final List<ResultStatuses> resultStatuses = daoSession.getResultStatusesDao().loadAll();
        if (resultStatuses == null || resultStatuses.size() == 0) {
            return resultHistoryList;
        }
        for (final ResultHistory resultHistory : resultHistories) {
            String causeName = StringUtil.EMPTY;
            for (final Causes cause : causes) {
                if (cause.id != null && cause.id.equals(resultHistory.fn_cause)) {
                    causeName = StringUtil.defaultEmptyString(cause.c_name);
                    break;
                }
            }
            String statusName = StringUtil.EMPTY;
            for (final ResultStatuses resultStatus : resultStatuses) {
                if (resultStatus.id != null && resultStatus.id.equals(resultHistory.fn_status)) {
                    statusName = StringUtil.defaultEmptyString(resultStatus.c_name);
                    break;
                }
            }
            final long time = DateUtil.getNonNullDateFromServerString(resultHistory.d_date).getTime();
            final String date = DateUtil.getDateToStringForUser(new Date(time));
            final ResultHistoryItem resultHistoryItem = new ResultHistoryItem(causeName, statusName, date, time);
            resultHistoryItemsList.add(resultHistoryItem);
        }
        Collections.sort(resultHistoryItemsList, (o1, o2) -> Long.compare(o2.time, o1.time));
        for (final ResultHistoryItem resultHistoryItem : resultHistoryItemsList) {
            String status = MobniusApplication.RESULT_STATUS + resultHistoryItem.statusName;
            if (StringUtil.isNotEmpty(resultHistoryItem.causeName)) {
                status = status + MobniusApplication.RESULT_CAUSE + resultHistoryItem.causeName;
            }
            resultHistoryList.add(new SimpleExpandableItem(resultHistoryItem.date, status));
        }
        return resultHistoryList;
    }

    public void disableImage(final @NonNull String imageId, int size) {
        final Attachments attachment = daoSession.getAttachmentsDao().load(imageId);
        if (attachment == null) {
            return;
        }
        attachment.b_disabled = true;
        if (attachment.objectOperationType == null ||
                !attachment.objectOperationType.equals(DbOperationType.CREATED)) {
            attachment.objectOperationType = DbOperationType.UPDATED;
            attachment.isSynchronization = false;
            attachment.n_size = size;
            attachment.dx_created = DateUtil.getNewDateStringForServer();
        }
        daoSession.getAttachmentsDao().update(attachment);
    }

    public int getInputMetersCount(final @NonNull String pointId) {
        final PointItem pointItem = getPointItem(pointId);
        if (pointItem == null) {
            return IntUtil.ZERO;
        }
        return pointItem.getMetersCount();
    }

    @VisibleForTesting
    public static void createTest(final @NonNull Context context) {
        final DaoSession daoSession = new DaoMaster(new DbOpenHelper(context, USER_ENG + Authorization.TEST_ID + ".db").getWritableDb()).newSession();
        daoSession.clear();
        dataManager = new DataManager(daoSession);
    }

}