package ru.mobnius.cic.ui.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.TestOnly;

import java.util.Date;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class RouteItem implements Parcelable, SearchResult {
    public static final String ROUTE_STATUS_DONE = "DONED";
    public static final String ROUTE_STATUS_RECEIVED = "RECEIVED";
    public static final String ROUTE_STATUS_IN_PROCESS = "PROCCESS";
    /**
     * идентификатор
     */
    @NonNull
    public final String id;
    /**
     * номер маршрута
     */
    @NonNull
    public final String name;
    /**
     * примечание
     */
    @NonNull
    public final String notice;

    /**
     * дата начала выполения
     */
    @NonNull
    public final Date dateStart;
    /**
     * Дата завершения маршрута
     */
    @NonNull
    public final Date dateEnd;
    /**
     * был ли продлен маршрут
     */
    public final boolean isExtended;
    /**
     * дата до которой продлен маршрут
     */
    @Nullable
    public final Date extended;

    /**
     * Сортировка
     */
    public final int order;

    /**
     * кол-во заданий
     */
    public final int totalPointsCount;
    /**
     * кол-во заданий
     */
    public final int donePointsCount;

    public final boolean isDraft;

    public boolean canBeDone = true;

    public RouteItem(final @NonNull String id,
                     final @NonNull String notice,
                     final @NonNull String name,
                     final @NonNull Date dateStart,
                     final @NonNull Date dateEnd,
                     final boolean isExtended,
                     final @Nullable Date extended,
                     final int order,
                     final int totalPointsCount,
                     final int donePointsCount,
                     final boolean isDraft) {
        this.id = id;
        this.notice = notice;
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.isExtended = isExtended;
        this.extended = extended;
        this.order = order;
        this.totalPointsCount = totalPointsCount;
        this.isDraft = isDraft;
        this.donePointsCount = donePointsCount;
    }

    protected RouteItem(final @NonNull Parcel in) {
        id = StringUtil.defaultEmptyString(in.readString());
        notice = StringUtil.defaultEmptyString(in.readString());
        name = StringUtil.defaultEmptyString(in.readString());
        isExtended = in.readByte() != 0;
        order = in.readInt();
        totalPointsCount = in.readInt();
        donePointsCount = in.readInt();
        isDraft = in.readByte() != 0;
        canBeDone = in.readByte() != 0;
        this.dateStart = new Date(in.readLong());
        this.dateEnd = new Date(in.readLong());
        this.extended = in.readLong() == 0 ? null : new Date(in.readLong());
    }


    public boolean isCurrentRoute() {
        if (DataManager.getInstance() == null) {
            return false;
        }
        return DataManager.getInstance().isCurrentRoute(id);
    }

    /**
     * Маршрут имеет указанный статус
     *
     * @param status статус
     * @return true - статус присутствует
     */
    //TODO: нужно вынести в отдельный файл, т.к. тут SQL код
    public boolean isRouteStatus(final @NonNull String status) {
        if (DataManager.getInstance() == null) {
            return false;
        }
        Cursor cursor = DataManager.getInstance().daoSession.getDatabase().rawQuery("select count(*) " +
                "from cd_route_history as rh " +
                "inner join cs_route_statuses as rs ON rh.fn_status = rs.id " +
                "where rh.fn_route = ? and rs.c_const = ?", new String[]{id, status});

        cursor.moveToFirst(); // переходим на первую строку
        long count = cursor.getLong(0);
        cursor.close();
        return count > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final @NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(notice);
        parcel.writeString(name);
        parcel.writeByte((byte) (isExtended ? 1 : 0));
        parcel.writeInt(order);
        parcel.writeInt(donePointsCount);
        parcel.writeInt(totalPointsCount);
        parcel.writeByte((byte) (isDraft ? 1 : 0));
        parcel.writeByte((byte) (canBeDone ? 1 : 0));
        parcel.writeLong(dateStart.getTime());
        parcel.writeLong(dateEnd.getTime());
        parcel.writeLong(extended == null ? 0 : extended.getTime());
    }

    @NonNull
    public static final Creator<RouteItem> CREATOR = new Creator<RouteItem>() {
        @NonNull
        @Override
        public RouteItem createFromParcel(@NonNull Parcel in) {
            return new RouteItem(in);
        }

        @NonNull
        @Override
        public RouteItem[] newArray(int size) {
            return new RouteItem[size];
        }
    };

    @Override
    public int getViewType() {
        return SearchResult.VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public String getHeaderName() {
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String getFirstLineText() {
        if (StringUtil.isNotEmpty(name)) {
            return MobniusApplication.ROUTES + StringUtil.COLON + StringUtil.SPACE + name;
        }
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getSecondLineText() {
        if (StringUtil.isNotEmpty(notice)) {
            return MobniusApplication.NOTICE + StringUtil.COLON + StringUtil.SPACE + notice;
        }
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getThirdLineText() {
        final String startDate = DateUtil.getNonNullDateTextMiddle(dateStart);
        if (StringUtil.isNotEmpty(startDate)) {
            return MobniusApplication.START_DATE + StringUtil.COLON + StringUtil.SPACE + startDate;
        }
        return StringUtil.EMPTY;
    }

    @NonNull
    @Override
    public String getFourthLineText() {
        final String endDate = DateUtil.getNonNullDateTextMiddle(dateEnd);
        if (StringUtil.isNotEmpty(endDate)) {
            return MobniusApplication.END_DATE + StringUtil.COLON + StringUtil.SPACE + endDate;
        }
        return StringUtil.EMPTY;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Nullable
    @Override
    public PointItem getPointItem() {
        return null;
    }

    @Nullable
    @Override
    public RouteItem getRouteItem() {
        return this;
    }
}
