package ru.mobnius.cic.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.TestOnly;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.IntUtil;
import ru.mobnius.simple_core.utils.JsonUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class PointItem implements SearchResult, Parcelable {
    private static final String DEVICE_NUMBER_JSON_KEY = "device_number";
    private static final String DEVICE_TYPE_NAME_JSON_KEY = "device_type_name";
    private static final String OWNER_JSON_KEY = "owner_name";
    private static final String ACCOUNT_NUMBER_JSON_KEY = "number";
    private static final String METER_JSON_KEY = "meters";
    private static final String VERSION_JSON_KEY = "version";
    private static final String META_JSON_KEY = "meta";
    private static final String DATA_JSON_KEY = "data";

    private final static DiffUtil.ItemCallback<PointItem> diffUtill = new DiffUtil.ItemCallback<PointItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PointItem oldItem, @NonNull PointItem newItem) {
            return StringUtil.equals(oldItem.id, newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull PointItem oldItem, @NonNull PointItem newItem) {
            if (oldItem.done != newItem.done) {
                return false;
            }
            if (oldItem.sync != newItem.sync) {
                return false;
            }
            return StringUtil.equals(oldItem.resultId, newItem.resultId);
        }
    };

    @NonNull
    public final String id;

    public int version;

    @NonNull
    public String deviceNumber;
    @NonNull
    public String deviceTypeName;
    @NonNull
    public String accountNumber;
    @NonNull
    public final String address;
    @NonNull
    public final String routeId;
    @NonNull
    public String owner;
    @NonNull
    public String metersJson;
    @NonNull
    public final String controlMetersJson;
    @NonNull
    public final String sealsJson;
    @Nullable
    public String resultId;

    public final double latitude;

    public final double longitude;

    public final boolean person;

    public final int order;

    /**
     * Было выполнено или нет
     */
    public boolean done;

    /**
     * Было синхронизировано или нет
     */
    public boolean sync;
    /**
     * Было отклонено или нет
     */
    public boolean reject;

    public PointItem(final @NonNull String pointId,
                     final @NonNull String routeId,
                     final @Nullable String resultId,
                     final @Nullable String address,
                     final @Nullable String jsonData,
                     final boolean person,
                     final boolean done,
                     final boolean sync,
                     final boolean reject,
                     final double latitude,
                     final double longitude,
                     final int order) {
        this.id = pointId;
        this.routeId = routeId;
        this.resultId = resultId;
        this.address = StringUtil.defaultEmptyString(address);
        this.done = done;
        this.sync = sync;
        this.reject = reject;
        this.person = person;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
        this.version = -1;
        this.deviceNumber = StringUtil.EMPTY;
        this.deviceTypeName = StringUtil.EMPTY;
        this.owner = StringUtil.EMPTY;
        this.accountNumber = StringUtil.EMPTY;
        this.metersJson = StringUtil.EMPTY;
        this.controlMetersJson = StringUtil.EMPTY;
        this.sealsJson = StringUtil.EMPTY;
        parseJson(jsonData);
    }


    public static final Creator<PointItem> CREATOR = new Creator<PointItem>() {
        @Override
        public PointItem createFromParcel(Parcel in) {
            return new PointItem(in);
        }

        @Override
        public PointItem[] newArray(int size) {
            return new PointItem[size];
        }
    };

    private void parseJson(final @Nullable String jsonString) {
        if (StringUtil.isEmpty(jsonString)) {
            return;
        }
        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            final JSONObject meta = JsonUtil.getNonNullJsonObject(jsonObject, META_JSON_KEY);
            version = JsonUtil.getIntFromJSONObject(meta, VERSION_JSON_KEY, -1);
            final JSONObject data = JsonUtil.getNonNullJsonObject(jsonObject, DATA_JSON_KEY);
            deviceNumber = JsonUtil.getNonNullString(data, DEVICE_NUMBER_JSON_KEY);
            deviceTypeName = JsonUtil.getNonNullString(data, DEVICE_TYPE_NAME_JSON_KEY);
            owner = JsonUtil.getNonNullString(data, OWNER_JSON_KEY);
            accountNumber = JsonUtil.getNonNullString(data, ACCOUNT_NUMBER_JSON_KEY);
            metersJson = JsonUtil.getNonNullString(data, METER_JSON_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public List<MeterItem> getMeters() {
        final List<MeterItem> meters = new ArrayList<>();
        try {
            final JSONArray array = new JSONArray(metersJson);
            for (int i = 0; i < array.length(); i++) {
                final JSONObject row = array.getJSONObject(i);
                final String id = JsonUtil.getNonNullString(row, JsonUtil.METER_ID_JSON_KEY);
                final Date previousDate = DateUtil.getNonNullMeterDateFromServerString(JsonUtil.getNonNullString(row, JsonUtil.METER_DATE_JSON_KEY));
                final Double previousValue = JsonUtil.getNullableDoubleFromJSONObject(row, JsonUtil.METER_VALUE_JSON_KEY);
                final String name = JsonUtil.getNonNullString(row, JsonUtil.NAME_JSON_KEY);
                final double digitRank = JsonUtil.getDoubleFromJSONObject(row, JsonUtil.METER_DIGIT_RANK_JSON_KEY, MeterItem.DIGIT_RANK_DEFAULT);
                final MeterItem meterItem = new MeterItem(id, name, previousValue, previousDate, null, null, digitRank);
                meters.add(meterItem);
            }
            return meters;
        } catch (JSONException e) {
            e.printStackTrace();
            return meters;
        }
    }

    public int getMetersCount() {
        try {
            final JSONArray array = new JSONArray(metersJson);
            return array.length();
        } catch (JSONException e) {
            e.printStackTrace();
            return IntUtil.ZERO;
        }
    }

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
        return MobniusApplication.SUBSCR_NAME + StringUtil.COLON + StringUtil.SPACE + owner;
    }

    @NonNull
    @Override
    public String getSecondLineText() {
        return MobniusApplication.ADDRESS + StringUtil.COLON + StringUtil.SPACE + address;
    }

    @NonNull
    @Override
    public String getThirdLineText() {
        return MobniusApplication.DEVICE_NUMBER + StringUtil.COLON + StringUtil.SPACE + deviceNumber;
    }

    @NonNull
    @Override
    public String getFourthLineText() {
        return MobniusApplication.ACCOUNT_NUMBER + StringUtil.COLON + StringUtil.SPACE + accountNumber;
    }

    @Override
    public int getPriority() {
        return SearchResult.POINT_ITEM_PRIORITY;
    }

    @Nullable
    @Override
    public PointItem getPointItem() {
        return this;
    }

    @Nullable
    @Override
    public RouteItem getRouteItem() {
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeInt(version);
        parcel.writeString(deviceNumber);
        parcel.writeString(deviceTypeName);
        parcel.writeString(accountNumber);
        parcel.writeString(address);
        parcel.writeString(routeId);
        parcel.writeString(owner);
        parcel.writeString(metersJson);
        parcel.writeString(controlMetersJson);
        parcel.writeString(sealsJson);
        parcel.writeString(resultId);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeByte((byte) (person ? 1 : 0));
        parcel.writeInt(order);
        parcel.writeByte((byte) (done ? 1 : 0));
        parcel.writeByte((byte) (sync ? 1 : 0));
        parcel.writeByte((byte) (reject ? 1 : 0));
    }

    protected PointItem(Parcel in) {
        id = StringUtil.defaultEmptyString(in.readString());
        version = in.readInt();
        deviceNumber = StringUtil.defaultEmptyString(in.readString());
        deviceTypeName = StringUtil.defaultEmptyString(in.readString());
        accountNumber = StringUtil.defaultEmptyString(in.readString());
        address = StringUtil.defaultEmptyString(in.readString());
        routeId = StringUtil.defaultEmptyString(in.readString());
        owner = StringUtil.defaultEmptyString(in.readString());
        metersJson = StringUtil.defaultEmptyString(in.readString());
        controlMetersJson = StringUtil.defaultEmptyString(in.readString());
        sealsJson = StringUtil.defaultEmptyString(in.readString());
        resultId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        person = in.readByte() != 0;
        order = in.readInt();
        done = in.readByte() != 0;
        sync = in.readByte() != 0;
        reject = in.readByte() != 0;
    }

    @TestOnly
    public static PointItem getTestInstance() {
        return new PointItem("POINT_ID",
                "ROUTE_ID",
                "RESULT_ID",
                StringUtil.EMPTY,
                StringUtil.EMPTY,
                false,
                false,
                false,
                false,
                0D,
                0D,
                0);
    }
    @TestOnly
    public static PointItem getRandomTestInstance() {
        return new PointItem(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                false,
                false,
                false,
                false,
                0D,
                0D,
                0);
    }

    @NonNull
    public static DiffUtil.ItemCallback<PointItem> getDiffUtill() {
        return diffUtill;
    }

}